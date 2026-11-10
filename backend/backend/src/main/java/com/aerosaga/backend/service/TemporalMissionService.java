package com.aerosaga.backend.service;

import com.aerosaga.workflow.DroneMissionWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowExecutionDescription;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TemporalMissionService {

    private final WorkflowClient client;

    public TemporalMissionService(WorkflowClient client) {
        this.client = client;
    }

    public String startMission(String droneId) {
        String workflowId = "drone-mission-" + UUID.randomUUID();

        WorkflowOptions options = WorkflowOptions.newBuilder()
                .setTaskQueue("DRONE_TASK_QUEUE")
                .setWorkflowId(workflowId)
                .build();

        DroneMissionWorkflow workflow = client.newWorkflowStub(DroneMissionWorkflow.class, options);
        WorkflowClient.start(workflow::executeMission, droneId);

        return workflowId;
    }

    public String getMissionStatus(String workflowId) {
        try {
            DroneMissionWorkflow workflow = client.newWorkflowStub(DroneMissionWorkflow.class, workflowId);
            WorkflowStub stub = WorkflowStub.fromTyped(workflow);
            WorkflowExecutionDescription description = stub.describe();
            if (isRunning(description)) {
                try {
                    return workflow.getMissionStatus() + " (Execution: " + description.getStatus().name() + ")";
                } catch (Exception queryException) {
                    description = stub.describe();
                    if (isRunning(description)) {
                        throw queryException;
                    }
                }
            }

            return formatFinalStatus(stub, description);
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    public String abortMission(String workflowId) {
        try {
            DroneMissionWorkflow workflow = client.newWorkflowStub(DroneMissionWorkflow.class, workflowId);
            WorkflowStub stub = WorkflowStub.fromTyped(workflow);
            WorkflowExecutionDescription description = stub.describe();
            if (!isRunning(description)) {
                return "Mission already finished: " + formatFinalStatus(stub, description);
            }

            workflow.abortMission();
            return "Abort signal sent successfully!";
        } catch (Exception e) {
            String status = getMissionStatus(workflowId);
            if (!status.startsWith("ERROR:")) {
                return "Mission already finished: " + status;
            }
            return "ERROR: " + e.getMessage();
        }
    }

    private boolean isRunning(WorkflowExecutionDescription description) {
        return description.getStatus() != null
                && "WORKFLOW_EXECUTION_STATUS_RUNNING".equals(description.getStatus().name());
    }

    private String formatFinalStatus(WorkflowStub stub, WorkflowExecutionDescription description) {
        String executionStatus = description.getStatus() == null ? "UNKNOWN" : description.getStatus().name();
        if ("WORKFLOW_EXECUTION_STATUS_COMPLETED".equals(executionStatus)) {
            String result = stub.getResult(String.class);
            String finalPhase = result != null && result.toLowerCase().contains("mission aborted")
                    ? "ABORTED"
                    : "COMPLETED";
            return finalPhase + " (Execution: " + executionStatus + ")";
        }

        return executionStatus.replace("WORKFLOW_EXECUTION_STATUS_", "")
                + " (Execution: " + executionStatus + ")";
    }
}