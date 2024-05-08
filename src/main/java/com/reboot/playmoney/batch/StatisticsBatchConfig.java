package com.reboot.playmoney.batch;

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
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class StatisticsBatchConfig {


    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final VideoViewStatsRepository videoViewStatsRepository;
    private final EntityManagerFactory entityManagerFactory;



    // 주간 및 월간 통계를 계산하는 Flow 정의
    @Bean
    public Job videoStatisticsJob(Step weeklyVideoViewStatsStep, Step monthlyVideoViewStatsStep) {
        log.info("Starting video statistics job");

        return new JobBuilder("videoStatisticsJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(weeklyVideoViewStatsStep)
//                .next(monthlyVideoViewStatsStep)
                .build();
    }

    // 주간 통계를 계산하는 Step
    @Bean
    @JobScope
    public Step weeklyVideoViewStatsStep(
            ItemReader<VideoViewStats> videoViewStatsJpaPagingItemReader,
            ItemProcessor<VideoViewStats, VideoViewStats> weeklyVideoViewStatsItemProcessor,
            ItemWriter<VideoViewStats> videoViewStatsJpaItemWriter
    ) {
        log.info("Starting weekly video view stats step");
        return new StepBuilder("weeklyVideoViewStatsStep", jobRepository)
                .<VideoViewStats, VideoViewStats>chunk(10, transactionManager)
                .reader(videoViewStatsJpaPagingItemReader)
                .processor(weeklyVideoViewStatsItemProcessor)
                .writer(videoViewStatsJpaItemWriter)
                .build();
    }

/*    // 월간 통계를 계산하는 Step
    @Bean
    public Step monthlyVideoViewStatsStep(
            JpaPagingItemReader<VideoViewStats> videoViewStatsJpaPagingItemReader,
            ItemProcessor<VideoViewStats, VideoViewStats> monthlyVideoViewStatsItemProcessor,
            JpaItemWriter<VideoViewStats> videoViewStatsJpaItemWriter
    ) {
        log.info("Starting monthly video view stats step");
        return new StepBuilder("monthlyVideoViewStatsStep", jobRepository)
                .<VideoViewStats, VideoViewStats>chunk(10, transactionManager)
                .reader(videoViewStatsJpaPagingItemReader)
                .processor(monthlyVideoViewStatsItemProcessor)
                .writer(videoViewStatsJpaItemWriter)
                .build();
    }*/



    // JPA PagingItemReader 정의
    @Bean
    @StepScope
    public ItemReader<VideoViewStats> videoViewStatsJpaPagingItemReader() {
        log.info("Starting video view stats jpa paging item reader");
        LocalDate startDate = LocalDate.now().minusDays(6).with(DayOfWeek.MONDAY);
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
        return dailyStats -> {
                LocalDate startDateOfWeek = dailyStats.getStartDate().with(DayOfWeek.MONDAY);
                LocalDate endDateOfWeek = dailyStats.getStartDate().with(DayOfWeek.SUNDAY);

                // 주간 데이터가 이미 존재하는지 확인
                boolean exists = videoViewStatsRepository.existsByVideoAndCategoryAndStartDateAndEndDate(
                        dailyStats.getVideo(), VideoViewStats.Category.WEEK, startDateOfWeek, endDateOfWeek
                );

                if (exists) {
                    // 이미 주간 데이터가 존재하는 경우 null을 반환
                    return null;
                }

                Integer weeklyViewCount = videoViewStatsRepository.sumDailyViewsForPeriod(
                        dailyStats.getVideo().getVideoNumber(), startDateOfWeek, endDateOfWeek);

                return new VideoViewStats(
                        dailyStats.getVideo(),startDateOfWeek,
                        endDateOfWeek,VideoViewStats.Category.WEEK,
                        weeklyViewCount != null ? weeklyViewCount : 0);
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

    // JPA ItemWriter 정의
    @Bean
    @StepScope
    public ItemWriter<VideoViewStats> videoViewStatsJpaItemWriter() {
        log.info("Starting video view stats jpa item writer");
        return new JpaItemWriterBuilder<VideoViewStats>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

//    // 매주 금요일마다 주간 정산을 한다.
//    public JobExecutionDecider isFridayDecider() {
//        return (jobExecution, stepExecution) -> {
//            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
//            final String targetDate = stepExecution.getJobParameters().getString("targetDate");
//            final LocalDate date = LocalDate.parse(targetDate, formatter);
//
//            if (date.getDayOfWeek() != DayOfWeek.FRIDAY) {
//                return new FlowExecutionStatus("NOOP");
//            }
//
//            return FlowExecutionStatus.COMPLETED;
//        };
//    }
//
//    // 매두 1일마다 월간 정산을 한다.


}