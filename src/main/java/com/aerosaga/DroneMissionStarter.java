package com.aerosaga;

import com.aerosaga.workflow.DroneMissionWorkflow;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;

public class DroneMissionStarter {

    public static void main(String[] args) {

        WorkflowServiceStubs service =
                WorkflowServiceStubs.newLocalServiceStubs();

        WorkflowClient client =
                WorkflowClient.newInstance(service);

        WorkflowOptions options =
                WorkflowOptions.newBuilder()
                        .setTaskQueue("DRONE_TASK_QUEUE")
                        .setWorkflowId("drone-mission-01")
                        .build();

        DroneMissionWorkflow workflow =
                client.newWorkflowStub(
                        DroneMissionWorkflow.class,
                        options
                );

        String result = workflow.executeMission("DRONE-01");

        System.out.println(result);
    }
}