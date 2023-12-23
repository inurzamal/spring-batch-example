package com.javatechie.spring.batch.config;

import com.javatechie.spring.batch.entity.Customer;
import com.javatechie.spring.batch.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.FileSystemResource;

@AllArgsConstructor
public class MyTasklet implements Tasklet {

    private CustomerRepository customerRepository;

    private FlatFileItemReader<Customer> reader;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        // Set the resource for the reader
        reader.setResource(new FileSystemResource(TaskletConfig.FILE_TO_READ_FROM));

        // Open the reader
        reader.open(chunkContext.getStepContext().getStepExecution().getExecutionContext());

        // Read and process each item from the CSV file
        Customer customer = null;
        do {
            customer = reader.read();
            if (customer != null) {
                // Your processing logic here
                // Example: Save to the database
                customerRepository.save(customer);
            }
        } while (customer != null);
        reader.close();
        return RepeatStatus.FINISHED;
    }
}

