package com.javatechie.spring.batch.config;

import com.javatechie.spring.batch.entity.Customer;
import com.javatechie.spring.batch.repository.CustomerRepository;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.FileSystemResource;

public class MyTasklet implements Tasklet {

    private final CustomerRepository customerRepository;

    public MyTasklet(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    private static final String FILE_TO_READ_FROM = "src/main/resources/customers.csv";

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        // Create a new instance of the reader
        FlatFileItemReader<Customer> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource(FILE_TO_READ_FROM));
        reader.setName("csvReader"); //any name
        reader.setLinesToSkip(1);
        reader.setLineMapper(lineMapper());

        // Open the reader
        reader.open(chunkContext.getStepContext().getStepExecution().getExecutionContext());

        // Read and process each item from the CSV file
        Customer customer = null;
        do {
            customer = reader.read();
            if (customer != null) {
                // Your processing logic here, Example: Save to the database
                customerRepository.save(customer);
            }
        } while (customer != null);

        // Close the reader
        reader.close();

        return RepeatStatus.FINISHED;
    }

    private LineMapper<Customer> lineMapper() {
        DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id", "firstName", "lastName", "email", "gender", "contactNo", "country", "dob");

        BeanWrapperFieldSetMapper<Customer> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Customer.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }
}
