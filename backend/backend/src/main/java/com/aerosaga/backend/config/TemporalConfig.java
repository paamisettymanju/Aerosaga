package com.aerosaga.backend.config;

import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TemporalConfig {

    @Bean
    public WorkflowServiceStubs workflowServiceStubs() {
        return WorkflowServiceStubs.newLocalServiceStubs();
    }


    @Bean
    public WorkflowClient workflowClient(
            WorkflowServiceStubs service) {
        return WorkflowClient.newInstance(service);
    }
}