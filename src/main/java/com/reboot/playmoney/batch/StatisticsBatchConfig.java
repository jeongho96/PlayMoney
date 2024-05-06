package com.reboot.playmoney.batch;

import com.reboot.playmoney.repository.VideoViewStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class BatchConfiguration {


    private final VideoViewStatsRepository videoViewStatsRepository;

    @Bean
    public Job job(JobRepository jobRepository, Step findTop5VideosWeekly) throws Exception {
        return new JobBuilder("StatisticJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(findTop5VideosWeekly) // Job 시작 Step 설정
                .build();
    }

    @Bean
    public Step findTop5VideosWeekly(JobRepository jobRepository, Tasklet weeklyTopViewsTasklet,PlatformTransactionManager transactionManager) {
        return new StepBuilder("findTop5VideosWeekly", jobRepository)
                .tasklet(weeklyTopViewsTasklet) // Tasklet 설정
                .transactionManager(transactionManager)
                .build();
    }



    @Bean
    public Tasklet weeklyTopViewsTasklet(DataSource dataSource) {
        return (contribution, chunkContext) -> {
            LocalDate now = LocalDate.now();
            LocalDate startDate = now.minusDays(now.getDayOfWeek().getValue() % 7);
            LocalDate endDate = startDate.plusWeeks(1);
            List<Object[]> results =
                    videoViewStatsRepository.findTop5VideosWithMostViews(startDate, endDate, PageRequest.of(0, 5));
            results.forEach(row -> {
                System.out.printf("videoId: %s, views: %s%n", row[0], row[1]);
            });
            return RepeatStatus.FINISHED;
        };
    }


}