package com.reboot.playmoney.batch.config;

import com.reboot.playmoney.batch.PeriodSalesItemDBWriter;
import com.reboot.playmoney.domain.DayCategory;
import com.reboot.playmoney.domain.Sales;
import com.reboot.playmoney.domain.Video;
import com.reboot.playmoney.repository.SalesRepository;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
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
public class SalesBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;

    private final PeriodSalesItemDBWriter periodSalesItemDBWriter;
    private final SalesRepository salesRepository;



    @Bean
    public Job periodSalesJob(@Qualifier("periodSalesStep") Step periodSalesStep) {
        log.info("Starting settlement weekly job");
        return new JobBuilder("periodSalesJob", jobRepository)
                .start(periodSalesStep)
                .build();
    }


    // 주간 ,월간 통계를 계산하는 Step
    @Bean
    @JobScope
    public Step periodSalesStep(
            JpaPagingItemReader<Sales> salesJpaPagingItemReader,
            ItemProcessor<Sales, Sales> periodSalesItemProcessor
    ) {
        log.info("Starting weekly sales step");
        return new StepBuilder("periodSalesStep", jobRepository)
                .<Sales, Sales>chunk(10, transactionManager)
                .reader(salesJpaPagingItemReader)
                .processor(periodSalesItemProcessor)
                .writer(periodSalesItemDBWriter)
                .build();
    }




    // JPA PagingItemReader 정의
    @Bean
    @StepScope
    public JpaPagingItemReader<Sales> salesJpaPagingItemReader(
            @Value("#{jobParameters['dateTime']}") LocalDateTime dateTime,
            @Value("#{jobParameters['statisticsType']}") String dateType
    ) {
        log.info("Starting sales jpa paging item reader");
        LocalDate date = dateTime.toLocalDate();
        LocalDate startDate = date.minusDays(7).with(DayOfWeek.MONDAY);;
        LocalDate endDate = startDate.plusDays(6);

        if(dateType.equals("month")){
            startDate = date.minusMonths(1).withDayOfMonth(1); // 직전 월의 첫째 날
            endDate = startDate.plusMonths(1).minusDays(1); // 해당 월의 마지막 일
        }


        Map<String, Object> parameters = new HashMap<>();
        parameters.put("category", DayCategory.DAY);
        parameters.put("startDate", startDate);
        parameters.put("endDate", endDate);


        return new JpaPagingItemReaderBuilder<Sales>()
                .name("salesJpaPagingItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(10)
                .queryString("SELECT v FROM Sales v WHERE v.category = :category " +
                        "AND v.startDate BETWEEN :startDate AND :endDate " +
                        "ORDER BY v.startDate ASC")
                .parameterValues(parameters)
                .build();
    }


    // 주간, 월간 통계를 계산하는 Processor 정의
    @Bean
    @StepScope
    ItemProcessor<Sales, Sales> periodSalesItemProcessor(
            @Value("#{jobParameters['statisticsType']}") String dateType
    ) {
        log.info("Starting weekly sales item processor");
        ConcurrentHashMap<Long, Sales> salesCache = new ConcurrentHashMap<>();
        return dailySales -> {
            LocalDate startDate = dailySales.getStartDate().with(DayOfWeek.MONDAY);
            LocalDate endDate = dailySales.getStartDate().with(DayOfWeek.SUNDAY);
            Video video = dailySales.getVideo();
            Sales existingSales = salesRepository.findByVideoAndStartDateAndEndDateAndCategory
                    (video, startDate, endDate, DayCategory.WEEK);

            if(dateType.equals("month")){
                startDate = dailySales.getStartDate().withDayOfMonth(1);
                endDate = startDate.plusMonths(1).minusDays(1);;
                existingSales = salesRepository.findByVideoAndStartDateAndEndDateAndCategory
                        (video, startDate, endDate, DayCategory.MONTH);
            }




            if(existingSales != null){
                // 이미 존재하고, chunk 범위 밖의 값이 있다면 업데이트.
                existingSales.setVideoSaleAmount(existingSales.getVideoSaleAmount() + dailySales.getVideoSaleAmount());
                existingSales.setAdSaleAmount(existingSales.getAdSaleAmount() + dailySales.getAdSaleAmount());
                return existingSales;
            }

            final Long videoNumber = dailySales.getVideo().getVideoNumber();
            // 람다함수에서 쓰기 위해 가공.
            LocalDate finalStartDate = startDate;
            LocalDate finalEndDate = endDate;
            DayCategory dayCategory = dateType.equals("month") ? DayCategory.MONTH : DayCategory.WEEK;

            salesCache.compute(videoNumber, (key, sales) -> {
                if(sales == null){
                    return Sales.builder()
                            .member(dailySales.getMember())
                            .video(dailySales.getVideo())
                            .videoSaleAmount(dailySales.getVideoSaleAmount())
                            .adSaleAmount(dailySales.getAdSaleAmount())
                            .category(dayCategory)
                            .startDate(finalStartDate)
                            .endDate(finalEndDate)
                            .build();

                }else{
                    // chunk 단위 내에서의 값 업데이트
                    log.info("기존 Video sales 누계 : {}", sales.getVideoSaleAmount());
                    log.info("기존 Ad sales 누계 : {}", sales.getAdSaleAmount());
                    log.info("추가 Video Sales 값 : {}", dailySales.getVideoSaleAmount());
                    log.info("추가 Ad sales 값 : {}", dailySales.getAdSaleAmount());
                    sales.setVideoSaleAmount(sales.getVideoSaleAmount() + dailySales.getVideoSaleAmount());
                    sales.setAdSaleAmount(sales.getAdSaleAmount() + dailySales.getAdSaleAmount());
                    log.info("정산 금액 : {} | {}", sales.getVideoSaleAmount(),sales.getAdSaleAmount());
                    return sales;
                }
            });
            return salesCache.get(videoNumber);
        };
    }




}