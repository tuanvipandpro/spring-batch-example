package com.tuanlm.example.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class JobCompletionNotificationListener implements JobExecutionListener {
    
    private static final Logger logger = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    @Override
    public void beforeJob (JobExecution jobExecution){
        logger.info(jobExecution.getJobInstance().getJobName() + " is start !!!");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            logger.info(jobExecution.getJobInstance().getJobName() + " is finish !!!");
            logger.info(jobExecution.getCreateTime() + " -> " + jobExecution.getEndTime());
        }
    }
}
