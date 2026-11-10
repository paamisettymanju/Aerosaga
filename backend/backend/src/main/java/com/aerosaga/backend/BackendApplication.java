package com.aerosaga.backend;

import com.aerosaga.activity.DroneActivitiesImpl;
import com.aerosaga.workflow.DroneMissionWorkflowImpl;
import io.temporal.client.WorkflowClient;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Bean
    public WorkerFactory workerFactory(WorkflowClient client) {
        WorkerFactory factory = WorkerFactory.newInstance(client);
        try {
            Worker worker = factory.newWorker("DRONE_TASK_QUEUE");
            worker.registerWorkflowImplementationTypes(DroneMissionWorkflowImpl.class);
            worker.registerActivitiesImplementations(new DroneActivitiesImpl());
            factory.start();
            System.out.println("Temporal worker started for DRONE_TASK_QUEUE");
        } catch (Exception ex) {
            System.err.println("Temporal worker could not be started. Start the Temporal dev server before using mission endpoints.");
            System.err.println(ex.getMessage());
        }
        return factory;
    }
}
