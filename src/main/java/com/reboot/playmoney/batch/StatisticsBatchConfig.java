package com.reboot.playmoney.batch;

import com.reboot.playmoney.domain.Video;
import com.reboot.playmoney.domain.VideoViewStats;
import com.reboot.playmoney.repository.VideoViewStatsRepository;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class StatisticsBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;
    private final VideoViewStatsItemDBWriter writerConfig;
    private final VideoViewStatsRepository videoViewStatsRepository;



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
            @Value("#{jobParameters['dateTime']}") LocalDateTime dateTime,
            @Value("#{jobParameters['statisticsType']}") String dateType
    ) {
        log.info("Starting video view stats jpa paging item reader");
        LocalDate date = dateTime.toLocalDate();
        LocalDate startDate = date.minusDays(7).with(DayOfWeek.MONDAY);;
        LocalDate endDate = startDate.plusDays(6);

        if(dateType.equals("month")){
            startDate = date.minusMonths(1).withDayOfMonth(1); // 직전 월의 첫째 날
            endDate = startDate.plusMonths(1).minusDays(1); // 해당 월의 마지막 일
        }


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

            Video video = dailyStats.getVideo();
            VideoViewStats existingStats = videoViewStatsRepository.findByVideoAndStartDateAndEndDateAndCategory
                    (video, startDateOfWeek, endDateOfWeek, VideoViewStats.Category.WEEK);

            if(existingStats != null){
                return existingStats;
            }

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


    // 월간 통계를 계산하는 Processor 정의
    @Bean
    ItemProcessor<VideoViewStats, VideoViewStats> monthlyVideoViewStatsItemProcessor() {
        log.info("Starting monthly video view stats item processor");
        ConcurrentHashMap<Long, VideoViewStats> videoStatisticsCache = new ConcurrentHashMap<>();
        return dailyStats -> {
            LocalDate startDateOfMonth = dailyStats.getStartDate().withDayOfMonth(1);
            LocalDate endDateOfMonth = startDateOfMonth.plusMonths(1).minusDays(1);;

            Video video = dailyStats.getVideo();
            VideoViewStats existingStats = videoViewStatsRepository.findByVideoAndStartDateAndEndDateAndCategory
                    (video, startDateOfMonth, endDateOfMonth, VideoViewStats.Category.MONTH);

            if(existingStats != null){
                return existingStats;
            }

            final Long videoNumber = dailyStats.getVideo().getVideoNumber();
            videoStatisticsCache.compute(videoNumber, (key, videoViewStats) -> {
                if(videoViewStats == null){
                    return new VideoViewStats(
                            dailyStats.getVideo(),
                            startDateOfMonth,
                            endDateOfMonth,
                            VideoViewStats.Category.MONTH,
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




}