package com.aerosaga.backend.config;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TemporalConfig {

    @Value("${aerosaga.temporal.target:localhost:7233}")
    private String temporalTarget;

    @Value("${aerosaga.temporal.namespace:default}")
    private String temporalNamespace;

    @Bean
    public WorkflowServiceStubs workflowServiceStubs() {
        return WorkflowServiceStubs.newServiceStubs(
                WorkflowServiceStubsOptions.newBuilder()
                        .setTarget(temporalTarget)
                        .build()
        );
    }

    @Bean
    public WorkflowClient workflowClient(
            WorkflowServiceStubs service) {
        WorkflowClientOptions options = WorkflowClientOptions.newBuilder()
                .setNamespace(temporalNamespace)
                .build();
        return WorkflowClient.newInstance(service, options);
    }
}