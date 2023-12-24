package com.javatechie.spring.batch.config;

import com.javatechie.spring.batch.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
@AllArgsConstructor
public class TaskletConfig {

    public static final String JOB_NAME = "importCustomers";

    private JobBuilderFactory jobBuilderFactory;
    private StepBuilderFactory stepBuilderFactory;
    private CustomerRepository customerRepository;


    @Bean
    public Tasklet myTasklet() {
        return new MyTasklet(customerRepository);
    }

    @Bean
    public Step myStep() {
        return stepBuilderFactory.get("myStep")
                .tasklet(myTasklet())  // Use the Tasklet bean here
                .build();
    }

    @Bean
    public Job myJob() {
        return jobBuilderFactory.get(JOB_NAME)
                .start(myStep())  // Include the new step in the job
                .build();
    }

}
