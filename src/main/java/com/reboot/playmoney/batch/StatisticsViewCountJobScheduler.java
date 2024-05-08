package com.reboot.playmoney.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class StatisticsViewCountJobScheduler {

    private final JobLauncher jobLauncher;

    private final Job videoWeeklyStatisticsJob;
    private final Job videoMonthlyStatisticsJob;


    @Scheduled(cron = "0 0 1 ? * MON *") // 매주 월요일 새벽 1시
//    @Scheduled(cron = "0 * * * * ?") // 매 1분 마다(테스트로)
    public void jobWeeklyScheduled() throws JobParametersInvalidException, JobExecutionAlreadyRunningException,
            JobRestartException, JobInstanceAlreadyCompleteException {

        // The current date in LocalDateTime format
        // 날짜가 아니라 localDateTime으로 받는 이유는
        // 동일한 잡 파라미터 사용시 에러가 나기 때문에 그걸 방지.
        LocalDateTime localDate = LocalDateTime.now();

        // Pass the current Date as a job parameter
        JobParameters parameters = new JobParametersBuilder()
                .addLocalDateTime("dateTime", localDate)
                .addString("statisticsType", "week")
                .toJobParameters();

        JobExecution jobExecution = jobLauncher.run(videoWeeklyStatisticsJob, parameters);

        while (jobExecution.isRunning()) {
            log.info("...");
        }

        log.info("Job Execution: {}", jobExecution.getStatus());
        log.info("Job getJobConfigurationName: {}", jobExecution.getJobInstance().getJobName());
        log.info("Job getJobId: {}", jobExecution.getJobId());
        log.info("Job getExitStatus: {}", jobExecution.getExitStatus());
        log.info("Job getJobInstance: {}", jobExecution.getJobInstance());
        log.info("Job getStepExecutions: {}", jobExecution.getStepExecutions());
        log.info("Job getLastUpdated: {}", jobExecution.getLastUpdated());
        log.info("Job getFailureExceptions: {}", jobExecution.getFailureExceptions());

    }

    @Scheduled(cron = "0 0 2 1 * ?") // 매달 1일 새벽 2시
//    @Scheduled(cron = "5 * * * * ?") // 매 1분 마다(테스트로)
    public void jobMonthlyScheduled() throws JobParametersInvalidException, JobExecutionAlreadyRunningException,
            JobRestartException, JobInstanceAlreadyCompleteException {

        // The current date in LocalDateTime format
        LocalDateTime localDate = LocalDateTime.now();

        // Pass the current Date as a job parameter
        JobParameters parameters = new JobParametersBuilder()
                .addLocalDateTime("dateTime", localDate)
                .addString("statisticsType", "month")
                .toJobParameters();

        JobExecution jobExecution = jobLauncher.run(videoMonthlyStatisticsJob, parameters);

        while (jobExecution.isRunning()) {
            log.info("...");
        }

        log.info("Job Execution: {}", jobExecution.getStatus());
        log.info("Job getJobConfigurationName: {}", jobExecution.getJobInstance().getJobName());
        log.info("Job getJobId: {}", jobExecution.getJobId());
        log.info("Job getExitStatus: {}", jobExecution.getExitStatus());
        log.info("Job getJobInstance: {}", jobExecution.getJobInstance());
        log.info("Job getStepExecutions: {}", jobExecution.getStepExecutions());
        log.info("Job getLastUpdated: {}", jobExecution.getLastUpdated());
        log.info("Job getFailureExceptions: {}", jobExecution.getFailureExceptions());

    }


}
