package com.tuanlm.example.demo.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.tuanlm.example.demo.jobs.OrderFirstStep;

import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
public class OrderJobConfig {

    private final OrderFirstStep orderFirstStep;

    @Bean
    public Job runOrderJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("orderJob", jobRepository)
            .incrementer(new RunIdIncrementer())
            .listener(new JobCompletionNotificationListener())
            .flow(orderFirstStep.readCsvToDbStep(jobRepository, transactionManager))
            .end()
        .build();
    }
}
