package com.tuanlm.example.demo.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.tuanlm.example.demo.jobs.CustomerFirstStep;

import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
public class CustomerJobConfig {
    private final CustomerFirstStep customerFirstStep;

    @Bean
    public Job runCustomerJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("customerJob", jobRepository)
            .incrementer(new RunIdIncrementer())
            .listener(new JobCompletionNotificationListener())
            .flow(customerFirstStep.readCsvToDbStep(jobRepository, transactionManager))
            .end()
        .build();
    }
}
