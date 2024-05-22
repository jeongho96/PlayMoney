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
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
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
        return new StepBuilder("periodSalesStep", jobRepository)
                .<Sales, Sales>chunk(100, transactionManager)
                .reader(salesJpaPagingItemReader)
                .processor(periodSalesItemProcessor)
//                .taskExecutor(new SimpleAsyncTaskExecutor())
                .writer(jpaItemWriter())
                .build();
    }




    // JPA PagingItemReader 정의
    @Bean
    @StepScope
    public JpaPagingItemReader<Sales> salesJpaPagingItemReader(
            @Value("#{jobParameters['dateTime']}") LocalDateTime dateTime,
            @Value("#{jobParameters['statisticsType']}") String dateType
    ) {
        LocalDate date = dateTime.toLocalDate();
        LocalDate startDate = date.minusDays(7).with(DayOfWeek.MONDAY);
        LocalDate endDate = startDate.plusDays(6);

        if(dateType.equals("month")){
            startDate = date.minusMonths(1).withDayOfMonth(1); // 직전 월의 첫째 날
            endDate = startDate.plusMonths(1).minusDays(1); // 해당 월의 마지막 일
        }


        Map<String, Object> parameters = new HashMap<>();
        parameters.put("category", DayCategory.DAY);
        parameters.put("startDate", startDate);
        parameters.put("endDate", endDate);


        // 기간 별 일일 정산 데이터 읽어오기.
        return new JpaPagingItemReaderBuilder<Sales>()
                .name("salesJpaPagingItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(100)
//                .saveState(false)
                .queryString("SELECT v FROM Sales v WHERE v.category = :category " +
                        "AND v.startDate BETWEEN :startDate AND :endDate " +
                        "ORDER BY v.startDate ASC")
                .parameterValues(parameters)
                .build();
    }


    // 주간, 월간 통계를 계산하는 Processor 정의
    // 주간, 월간 통계를 계산하는 Processor 정의
    @Bean
    @StepScope
    ItemProcessor<Sales, Sales> periodSalesItemProcessor(
            @Value("#{jobParameters['statisticsType']}") String dateType
    ) {
        ConcurrentHashMap<Video, Sales> salesMap = new ConcurrentHashMap<>();

        return dailySales -> {
            LocalDate startDate;
            LocalDate endDate;
            Video video = dailySales.getVideo();

            if(dateType.equals("month")){
                startDate = dailySales.getStartDate().withDayOfMonth(1);
                endDate = startDate.plusMonths(1).minusDays(1);
            } else {
                endDate = dailySales.getStartDate().with(DayOfWeek.SUNDAY);
                startDate = dailySales.getStartDate().with(DayOfWeek.MONDAY);
            }

            Sales existingSales = salesMap.computeIfAbsent(video, v ->
                    salesRepository.findByVideoAndStartDateAndEndDateAndCategory(v, startDate, endDate, dateType.equals("month") ? DayCategory.MONTH : DayCategory.WEEK));

            // 이미 값이 존재하면.
            if(existingSales != null){
                existingSales.setVideoSaleAmount(existingSales.getVideoSaleAmount() + dailySales.getVideoSaleAmount());
                existingSales.setAdSaleAmount(existingSales.getAdSaleAmount() + dailySales.getAdSaleAmount());
                return existingSales;
            }

            // DB에는 없지만 캐시에만 존재하는 경우를 고려해서 2가지로 조건 확인.
            else{
                if(salesMap.containsKey(video)){
                    return salesMap.compute(video, (v, sales) -> {
                        if(sales == null) {
                            sales = new Sales();
                        }
                        sales.setVideoSaleAmount(sales.getVideoSaleAmount() + dailySales.getVideoSaleAmount());
                        sales.setAdSaleAmount(sales.getAdSaleAmount() + dailySales.getAdSaleAmount());
                        return sales;
                    });
                }

                else{
                    DayCategory dayCategory = dateType.equals("month") ? DayCategory.MONTH : DayCategory.WEEK;

                    return Sales.builder()
                            .member(dailySales.getMember())
                            .video(dailySales.getVideo())
                            .videoSaleAmount(dailySales.getVideoSaleAmount())
                            .adSaleAmount(dailySales.getAdSaleAmount())
                            .category(dayCategory)
                            .startDate(startDate)
                            .endDate(endDate)
                            .build();
                }

            }
        };
    }


    @Bean
    public ItemWriter<Sales> jpaItemWriter() {
        return new JpaItemWriterBuilder<Sales>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }
}