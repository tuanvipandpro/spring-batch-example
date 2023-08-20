package com.tuanlm.example.demo.jobs;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import com.tuanlm.example.demo.entity.Order;
import com.tuanlm.example.demo.repository.OrderRepository;

import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
public class OrderFirstStep {
    
    private OrderRepository orderRepository;

    public LineMapper<Order> lineOrderMapper() {
        DefaultLineMapper<Order> lineMapper = new DefaultLineMapper<>();
        
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("customerId", "itemId", "itemPrice", "itemName", "purchaseDate");

        BeanWrapperFieldSetMapper<Order> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Order.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return lineMapper;
    }


    public ItemReader<Order> reader() {
        return new FlatFileItemReaderBuilder<Order>()
            .name("orderItemReader")
            .resource(new ClassPathResource("orders.csv"))
            .linesToSkip(1)
            .lineMapper(lineOrderMapper())
            .build();
    }

    public ItemProcessor<Order, Order> processor() {
        return item -> item;
    }

    public ItemWriter<Order> writer() {
        return new RepositoryItemWriterBuilder<Order>()
            .repository(orderRepository)
            .methodName("save")
        .build();
    }

    public Step readCsvToDbStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("readCsvToDbStep", jobRepository)
            .<Order, Order> chunk(10, transactionManager)
            .reader(reader())
            .processor(processor())
            .writer(writer())
        .build();
    }
}
