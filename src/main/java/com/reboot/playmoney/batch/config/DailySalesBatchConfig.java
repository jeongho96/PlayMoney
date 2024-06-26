package com.reboot.playmoney.batch.config;


import com.reboot.playmoney.batch.DailyAdSalesProcessor;
import com.reboot.playmoney.batch.DailyVideoSalesProcessor;
import com.reboot.playmoney.batch.DailySalesItemDBWriter;
import com.reboot.playmoney.domain.AdViewStats;
import com.reboot.playmoney.domain.DayCategory;
import com.reboot.playmoney.domain.Sales;
import com.reboot.playmoney.domain.VideoViewStats;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DailySalesBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;

    private final DailyVideoSalesProcessor dailyVideoSalesProcessor;
    private final DailyAdSalesProcessor dailyAdSalesProcessor;
    private final DailySalesItemDBWriter dailySalesItemDBWriter;



    @Bean
    public Job dailyViewSalesJob(@Qualifier("dailyViewSalesStep") Step dailyViewSalesStep,
                                 @Qualifier("dailyAdSalesStep") Step dailyAdSalesStep) {
        log.info("Starting daily VideoView sales job");
        return new JobBuilder("dailyViewSalesJob", jobRepository)
                .start(viewTypeDecider())
                .next(viewTypeDecider()).on("Video").to(dailyViewSalesStep)
                .from(viewTypeDecider()).on("Ad").to(dailyAdSalesStep)
                .end()
                .build();
    }




    private JobExecutionDecider viewTypeDecider() {
        return (jobExecution, stepExecution) -> {
            String decision = jobExecution.getJobParameters().getString("statisticsType");

            if ("Video".equals(decision)) {
                return new FlowExecutionStatus("Video");
            } else if ("Ad".equals(decision)) {
                return new FlowExecutionStatus("Ad");
            } else {
                return new FlowExecutionStatus("UNKNOWN");
            }
        };
    }


    // 비디오 일일 조회수에 대한 정산.
    @Bean
    @JobScope
    public Step dailyViewSalesStep(
            JpaCursorItemReader<VideoViewStats> videoViewSalesJpaItemReader
    ) {
        log.info("Starting daily Video View sales step");

        return new StepBuilder("dailyViewSalesStep", jobRepository)
                .<VideoViewStats, Sales>chunk(10, transactionManager)
                .reader(videoViewSalesJpaItemReader)
                .processor(dailyVideoSalesProcessor)
                .writer(dailySalesItemDBWriter)
                .build();
    }

    // 비디오에 연관된 광고에 대한 일일 조회수 정산.
    @Bean
    @JobScope
    public Step dailyAdSalesStep(
            JpaPagingItemReader<AdViewStats> AdViewSalesJpaItemReader
    ) {
        log.info("Starting daily Advertisement sales step");

        return new StepBuilder("dailyAdSalesStep", jobRepository)
                .<AdViewStats, Sales>chunk(10, transactionManager)
                .reader(AdViewSalesJpaItemReader)
                .processor(dailyAdSalesProcessor)
                .writer(dailySalesItemDBWriter)
                .build();
    }

    // 동영상 일일 조회수 기반 조회.
    @Bean
    @StepScope
    public JpaCursorItemReader<VideoViewStats> videoViewSalesJpaItemReader(
            @Value("#{jobParameters['dateTime']}") LocalDateTime dateTime
    ) {
        log.info("Starting video view stats item reader");
        // 하루 전날 데이터 기준으로 잡기.
        LocalDate date = dateTime.toLocalDate().minusDays(1);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("category", DayCategory.DAY);
        parameters.put("startDate", date);
        parameters.put("endDate", date);


        return new JpaCursorItemReaderBuilder<VideoViewStats>()
                .name("videoViewStatsJpaPagingItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT v FROM VideoViewStats v WHERE v.category = :category " +
                        "AND v.startDate BETWEEN :startDate AND :endDate " +
                        "ORDER BY v.startDate ASC")
                .parameterValues(parameters)
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<AdViewStats> AdViewSalesJpaItemReader(
            @Value("#{jobParameters['dateTime']}") LocalDateTime dateTime
    ) {
        log.info("Starting Advertisement view stats jpa paging item reader");
        // 하루 전날 데이터 기준으로 잡기.
        LocalDate date = dateTime.toLocalDate().minusDays(1);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("category", DayCategory.DAY);
        parameters.put("startDate", date);
        parameters.put("endDate", date);


        return new JpaPagingItemReaderBuilder<AdViewStats>()
                .name("videoViewStatsJpaPagingItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(10)
                .queryString("SELECT v FROM AdViewStats v WHERE v.category = :category " +
                        "AND v.startDate BETWEEN :startDate AND :endDate " +
                        "ORDER BY v.startDate ASC")
                .parameterValues(parameters)
                .build();
    }


}
