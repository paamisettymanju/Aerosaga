package com.aerosaga.worker;

import com.aerosaga.activity.DroneActivitiesImpl;
import com.aerosaga.workflow.DroneMissionWorkflowImpl;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;

public class DroneWorker {

    public static void main(String[] args) {

        WorkflowServiceStubs service =
                WorkflowServiceStubs.newServiceStubs(
                        WorkflowServiceStubsOptions.newBuilder()
                                .setTarget("localhost:7233")
                                .build()
                );

        WorkflowClient client =
                WorkflowClient.newInstance(service);

        WorkerFactory factory =
                WorkerFactory.newInstance(client);

        Worker worker =
                factory.newWorker("DRONE_TASK_QUEUE");

        worker.registerWorkflowImplementationTypes(
                DroneMissionWorkflowImpl.class
        );

        worker.registerActivitiesImplementations(
                new DroneActivitiesImpl()
        );

        factory.start();

        System.out.println("AeroSaga Drone Worker started!");
    }
}