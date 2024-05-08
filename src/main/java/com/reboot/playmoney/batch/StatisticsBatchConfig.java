package com.reboot.playmoney.batch;

import com.reboot.playmoney.domain.VideoViewStats;
import com.reboot.playmoney.repository.VideoViewStatsRepository;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class StatisticsBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;
    private final VideoViewStatsItemDBWriter writerConfig;



    @Bean
    public Job videoWeeklyStatisticsJob(Step weeklyVideoViewStatsStep) {
        log.info("Starting video count weekly job");

        return new JobBuilder("videoWeeklyStatisticsJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(weeklyVideoViewStatsStep)
                .build();
    }

    @Bean
    public Job videoMonthlyStatisticsJob(Step monthlyVideoViewStatsStep) {
        log.info("Starting video count monthly job");

        return new JobBuilder("videoMonthlyStatisticsJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(monthlyVideoViewStatsStep)
                .build();
    }

    // 주간 통계를 계산하는 Step
    @Bean
    @JobScope
    public Step weeklyVideoViewStatsStep(
            JpaPagingItemReader<VideoViewStats> videoViewStatsJpaPagingItemReader,
            ItemProcessor<VideoViewStats, VideoViewStats> weeklyVideoViewStatsItemProcessor
    ) {
        log.info("Starting weekly video view stats step");

        LocalDate startDate = LocalDate.now().minusDays(7).with(DayOfWeek.MONDAY);
        LocalDate endDate = startDate.plusDays(6);
        return new StepBuilder("weeklyVideoViewStatsStep", jobRepository)
                .<VideoViewStats, VideoViewStats>chunk(10, transactionManager)
                .reader(videoViewStatsJpaPagingItemReader)
                .processor(weeklyVideoViewStatsItemProcessor)
                .writer(writerConfig)
                .build();
    }

    // 월간 통계를 계산하는 Step
    @Bean
    public Step monthlyVideoViewStatsStep(
            JpaPagingItemReader<VideoViewStats> videoViewStatsJpaPagingItemReader,
            ItemProcessor<VideoViewStats, VideoViewStats> monthlyVideoViewStatsItemProcessor
    ) {
        log.info("Starting monthly video view stats step");
        return new StepBuilder("monthlyVideoViewStatsStep", jobRepository)
                .<VideoViewStats, VideoViewStats>chunk(10, transactionManager)
                .reader(videoViewStatsJpaPagingItemReader)
                .processor(monthlyVideoViewStatsItemProcessor)
                .writer(writerConfig)
                .build();
    }



    // JPA PagingItemReader 정의
    @Bean
    @StepScope
    public JpaPagingItemReader<VideoViewStats> videoViewStatsJpaPagingItemReader(
            @Value("#{jobParameters['date']}") LocalDate date
    ) {
        log.info("Starting video view stats jpa paging item reader");
        LocalDate startDate = date.minusDays(7).with(DayOfWeek.MONDAY);
        LocalDate endDate = startDate.plusDays(6);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("category", VideoViewStats.Category.DAY);
        parameters.put("startDate", startDate);
        parameters.put("endDate", endDate);


        return new JpaPagingItemReaderBuilder<VideoViewStats>()
                .name("videoViewStatsJpaPagingItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(10)
                .queryString("SELECT v FROM VideoViewStats v WHERE v.category = :category " +
                        "AND v.startDate BETWEEN :startDate AND :endDate " +
                        "ORDER BY v.startDate ASC")
                .parameterValues(parameters)
                .build();
    }


    // 주간 통계를 계산하는 Processor 정의
    @Bean
    @StepScope
    ItemProcessor<VideoViewStats, VideoViewStats> weeklyVideoViewStatsItemProcessor() {
        log.info("Starting weekly video view stats item processor");
        ConcurrentHashMap<Long, VideoViewStats> videoStatisticsCache = new ConcurrentHashMap<>();
        return dailyStats -> {
            LocalDate startDateOfWeek = dailyStats.getStartDate().with(DayOfWeek.MONDAY);
            LocalDate endDateOfWeek = dailyStats.getStartDate().with(DayOfWeek.SUNDAY);

            final Long videoNumber = dailyStats.getVideo().getVideoNumber();
            videoStatisticsCache.compute(videoNumber, (key, videoViewStats) -> {
                if(videoViewStats == null){
                    return new VideoViewStats(
                            dailyStats.getVideo(),
                            startDateOfWeek,
                            endDateOfWeek,
                            VideoViewStats.Category.WEEK,
                            dailyStats.getViewCount()
                    );

                }else{
                    videoViewStats.setViewCount(videoViewStats.getViewCount() + dailyStats.getViewCount());
                    return videoViewStats;
                }
            });
            return videoStatisticsCache.get(videoNumber);
        };
    }


    /*// 월간 통계를 계산하는 Processor 정의
    @Bean
    ItemProcessor<VideoViewStats, VideoViewStats> monthlyVideoViewStatsItemProcessor() {
        log.info("Starting monthly video view stats item processor");
        return videoViewStats -> {
            if (videoViewStats.getCategory().equals(VideoViewStats.Category.DAY)) {
                LocalDate startDate = videoViewStats.getStartDate().withDayOfMonth(1); // 해당 월의 첫째 날
                LocalDate endDate = startDate.plusMonths(1).minusDays(1); // 해당 월의 마지막 일

                // 해당 기간에 대한 월간 조회수 데이터가 이미 존재하는지 확인
                if (!videoViewStatsRepository.existsByVideo_VideoNumberAndCategoryAndStartDateAndEndDate(
                        videoViewStats.getVideo().getVideoNumber(),
                        VideoViewStats.Category.MONTH,
                        startDate,
                        endDate
                )) {
                    Integer monthlyViewCount = videoViewStatsRepository.sumDailyViewsForPeriod(
                            videoViewStats.getVideo().getVideoNumber(), startDate, endDate);

                    // 새로운 VideoViewStats 객체를 생성하여 월간 데이터를 저장합니다.
                    VideoViewStats newMonthlyStats = new VideoViewStats();
                    newMonthlyStats.setVideo(videoViewStats.getVideo());
                    newMonthlyStats.setStartDate(startDate);
                    newMonthlyStats.setEndDate(endDate);
                    newMonthlyStats.setCategory(VideoViewStats.Category.MONTH);
                    newMonthlyStats.setViewCount(monthlyViewCount != null ? monthlyViewCount : 0);

                    return newMonthlyStats;
                }
            }
            return null; // DAY 카테고리가 아닌 경우 null을 반환하여 처리하지 않습니다.
        };
    }*/




}