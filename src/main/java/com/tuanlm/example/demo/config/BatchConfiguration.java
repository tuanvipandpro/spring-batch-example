package com.tuanlm.example.demo.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.tuanlm.example.demo.entity.Order;
import com.tuanlm.example.demo.repository.OrderRepository;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilder;

    @Autowired
    public StepBuilderFactory stepBuilder;

    @Autowired
    private OrderRepository orderRepository;


    @Bean
    public ItemReader<Order> reader() {
        return new FlatFileItemReaderBuilder<Order>()
            .name("orderItemReader")
            .resource(new ClassPathResource("orders.csv"))
            .delimited()
            .names(new String[] {"CustomerId", "ItemId", "ItemPrice", "ItemName", "PurchaseDate"})
            .fieldSetMapper(new BeanWrapperFieldSetMapper<Order>(){
                {setTargetType(Order.class);}
            })
            .build();
    }

    @Bean
    public ItemProcessor<Order, Order> processor() {
        return new ItemProcessor<Order,Order>() {
            @Override
            @Nullable
            public Order process(@NonNull Order item) throws Exception {
                return item;
            }
        };
    }

    @Bean
    public ItemWriter<Order> writer() {
        return new RepositoryItemWriterBuilder<Order>()
            .repository(orderRepository)
            .methodName("save")
        .build();
    }

    @Bean
    public Step step1() {
        return stepBuilder.get("step1")
            .<Order, Order> chunk(10)
            .reader(reader())
            .processor(processor())
            .writer(writer())
        .build();
    }

    @Bean
    public Job importOrderJob() {
        return jobBuilder.get("importOrderJob")
            .incrementer(new RunIdIncrementer())
            .listener(new JobCompletionNotificationListener())
            .flow(step1())
            .end()
        .build();
    }
}
