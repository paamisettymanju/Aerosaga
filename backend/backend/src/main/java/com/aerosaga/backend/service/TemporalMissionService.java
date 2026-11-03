package com.aerosaga.backend.service;

import com.aerosaga.workflow.DroneMissionWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import io.temporal.client.WorkflowExecutionDescription;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TemporalMissionService {

    private final WorkflowClient client;

    public TemporalMissionService(WorkflowClient client) {
        this.client = client;
    }

    // Start a new drone mission
    public String startMission(String droneId) {

        String workflowId = "drone-mission-" + UUID.randomUUID();

        WorkflowOptions options = WorkflowOptions.newBuilder()
                .setTaskQueue("DRONE_TASK_QUEUE")
                .setWorkflowId(workflowId)
                .build();

        DroneMissionWorkflow workflow =
                client.newWorkflowStub(
                        DroneMissionWorkflow.class,
                        options
                );

        WorkflowClient.start(workflow::executeMission, droneId);

        return workflowId;
    }

    // Get mission status
    public String getMissionStatus(String workflowId) {

        try {
            DroneMissionWorkflow workflow =
                    client.newWorkflowStub(
                            DroneMissionWorkflow.class,
                            workflowId
                    );

            WorkflowExecutionDescription description =
                    WorkflowStub.fromTyped(workflow).describe();

            return description.getStatus().name();

        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    // Abort an existing drone mission
    public String abortMission(String workflowId) {

        try {
            DroneMissionWorkflow workflow =
                    client.newWorkflowStub(
                            DroneMissionWorkflow.class,
                            workflowId
                    );

            workflow.abortMission();

            return "Abort signal sent successfully!";

        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }
}