package com.reboot.playmoney.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class JobScheduler {

    private final JobLauncher jobLauncher;

    private final Job videoWeeklyStatisticsJob;
    private final Job videoMonthlyStatisticsJob;


//    @Scheduled(cron = "0 0 1 ? * MON *") // 매주 월요일 새벽 1시
//    @Scheduled(cron = "0 * * * * ?") // 매 1분 마다(테스트로)
    public void jobWeeklyScheduled() throws JobParametersInvalidException, JobExecutionAlreadyRunningException,
            JobRestartException, JobInstanceAlreadyCompleteException {

        // The current date in LocalDate format
        LocalDate localDate = LocalDate.now();

        // Pass the current Date as a job parameter
        JobParameters parameters = new JobParametersBuilder()
                .addLocalDate("date", localDate)
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


    public void jobMonthlyScheduled() throws JobParametersInvalidException, JobExecutionAlreadyRunningException,
            JobRestartException, JobInstanceAlreadyCompleteException {

        // The current date in LocalDate format
        LocalDate localDate = LocalDate.now();

        // Pass the current Date as a job parameter
        JobParameters parameters = new JobParametersBuilder()
                .addLocalDate("date", localDate)
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
