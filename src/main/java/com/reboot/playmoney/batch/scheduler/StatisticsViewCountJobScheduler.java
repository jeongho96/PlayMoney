package com.reboot.playmoney.batch.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;

@Configuration
@Slf4j
public class StatisticsViewCountJobScheduler {

    private final JobLauncher jobLauncher;
    @Qualifier("videoWeeklyStatisticsJob")
    private final Job videoWeeklyStatisticsJob;
    @Qualifier("videoMonthlyStatisticsJob")
    private final Job videoMonthlyStatisticsJob;

    public StatisticsViewCountJobScheduler(JobLauncher jobLauncher,
                          @Qualifier("videoWeeklyStatisticsJob") Job videoWeeklyStatisticsJob,
                          @Qualifier("videoMonthlyStatisticsJob") Job videoMonthlyStatisticsJob) {
        this.jobLauncher = jobLauncher;
        this.videoWeeklyStatisticsJob = videoWeeklyStatisticsJob;
        this.videoMonthlyStatisticsJob = videoMonthlyStatisticsJob;
    }

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

    @Scheduled(cron = "0 0 1 * * MON") // 매주 월요일 새벽 1시 실행
    public void jobWeeklyScheduled() throws JobParametersInvalidException, JobExecutionAlreadyRunningException,
            JobRestartException, JobInstanceAlreadyCompleteException {
        JobParameters parameters = createJobParameters("week");
        JobExecution jobExecution = jobLauncher.run(videoWeeklyStatisticsJob, parameters);
        while (jobExecution.isRunning()) {
            log.info("...");
        }
        logJobExecution(jobExecution);
    }

    @Scheduled(cron = "0 0 2 1 * ?") // 매달 1일 새벽 2시 실행
    public void jobMonthlyScheduled() throws JobParametersInvalidException, JobExecutionAlreadyRunningException,
            JobRestartException, JobInstanceAlreadyCompleteException {
        JobParameters parameters = createJobParameters("month");
        JobExecution jobExecution = jobLauncher.run(videoMonthlyStatisticsJob, parameters);
        while (jobExecution.isRunning()) {
            log.info("...");
        }
        logJobExecution(jobExecution);
    }
}