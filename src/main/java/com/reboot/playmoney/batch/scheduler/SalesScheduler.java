package com.reboot.playmoney.batch.scheduler;

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
public class SalesScheduler {
    private final JobLauncher jobLauncher;
    private final Job dailyViewSalesJob;
    private final Job periodSalesJob;

    private JobParameters createJobParameters(String statisticsType) {
        LocalDateTime localDate = LocalDateTime.now();
        return new JobParametersBuilder()
                .addLocalDateTime("dateTime", localDate)
                .addString("statisticsType", statisticsType)
                .toJobParameters();
    }

    private void logJobExecution(JobExecution jobExecution) {
        log.info("Job Execution: {}", jobExecution.getStatus());
        log.info("Job getJobConfigurationName: {}", jobExecution.getJobInstance().getJobName());
        log.info("Job getJobId: {}", jobExecution.getJobId());
        log.info("Job getExitStatus: {}", jobExecution.getExitStatus());
        log.info("Job getJobInstance: {}", jobExecution.getJobInstance());
        log.info("Job getStepExecutions: {}", jobExecution.getStepExecutions());
        log.info("Job getLastUpdated: {}", jobExecution.getLastUpdated());
        log.info("Job getFailureExceptions: {}", jobExecution.getFailureExceptions());
    }

    @Scheduled(cron = "0 0 1 * * ?") // 매일 새벽 1시에 수행
//    @Scheduled(cron = "*/20 * * * * ?") // 테스트용 매 20초 실행
    public void SalesJobDailyVideoViewScheduled() throws JobParametersInvalidException, JobExecutionAlreadyRunningException,
            JobRestartException, JobInstanceAlreadyCompleteException {
        JobParameters parameters = createJobParameters("Video");
        JobExecution jobExecution = jobLauncher.run(dailyViewSalesJob, parameters);
        while (jobExecution.isRunning()) {
            log.info("...");
        }
        logJobExecution(jobExecution);
    }

    @Scheduled(cron = "0 5 1 * * ?") // 매일 새벽 1시 5분에 수행
//    @Scheduled(cron = "*/30 * * * * ?") // 테스트용 매 30초 실행
    public void SalesJobDailyAdViewScheduled() throws JobParametersInvalidException, JobExecutionAlreadyRunningException,
            JobRestartException, JobInstanceAlreadyCompleteException {
        JobParameters parameters = createJobParameters("Ad");
        JobExecution jobExecution = jobLauncher.run(dailyViewSalesJob, parameters);
        while (jobExecution.isRunning()) {
            log.info("...");
        }
        logJobExecution(jobExecution);
    }

    @Scheduled(cron = "0 45 0 ? * MON") // 매주 월요일 새벽 00:45에 수행
//    @Scheduled(cron = "*/20 * * * * ?") // 테스트용 매 20초 실행
    public void SalesJobWeeklyScheduled() throws JobParametersInvalidException, JobExecutionAlreadyRunningException,
            JobRestartException, JobInstanceAlreadyCompleteException {
        JobParameters parameters = createJobParameters("week");
        JobExecution jobExecution = jobLauncher.run(periodSalesJob, parameters);
        while (jobExecution.isRunning()) {
            log.info("...");
        }
        logJobExecution(jobExecution);
    }

    @Scheduled(cron = "30 0 1 1 * ?") // 매달 1일 새벽 00:30에 수행
//    @Scheduled(cron = "*/30 * * * * ?") // 테스트용 매 20초 실행
    public void SalesJobMonthlyScheduled() throws JobParametersInvalidException, JobExecutionAlreadyRunningException,
            JobRestartException, JobInstanceAlreadyCompleteException {
        JobParameters parameters = createJobParameters("month");
        JobExecution jobExecution = jobLauncher.run(periodSalesJob, parameters);
        while (jobExecution.isRunning()) {
            log.info("...");
        }
        logJobExecution(jobExecution);
    }
}
