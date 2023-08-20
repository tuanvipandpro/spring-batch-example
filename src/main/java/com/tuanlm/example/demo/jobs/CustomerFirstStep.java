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

import com.tuanlm.example.demo.entity.Customer;
import com.tuanlm.example.demo.repository.CustomerRepository;

import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
public class CustomerFirstStep {
    private CustomerRepository CustomerRepository;

    public LineMapper<Customer> lineCustomerMapper() {
        DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>();
        
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id", "firstname", "lastname", "email", "gender", "contactNo", "country", "dob");

        BeanWrapperFieldSetMapper<Customer> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Customer.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return lineMapper;
    }


    public ItemReader<Customer> reader() {
        return new FlatFileItemReaderBuilder<Customer>()
            .name("customerItemReader")
            .resource(new ClassPathResource("customers.csv"))
            .linesToSkip(1)
            .lineMapper(lineCustomerMapper())
            .build();
    }

    public ItemProcessor<Customer, Customer> processor() {
        return item -> item;
    }

    public ItemWriter<Customer> writer() {
        return new RepositoryItemWriterBuilder<Customer>()
            .repository(CustomerRepository)
            .methodName("save")
        .build();
    }

    public Step readCsvToDbStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("readCustomerCsvToDbStep", jobRepository)
            .<Customer, Customer> chunk(10, transactionManager)
            .reader(reader())
            .processor(processor())
            .writer(writer())
        .build();
    }
}
