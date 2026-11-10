package com.aerosaga.workflow;

import com.aerosaga.activity.DroneActivitiesImpl;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.worker.Worker;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DroneMissionWorkflowTest {

    private static final String TASK_QUEUE = "TEST_DRONE_TASK_QUEUE";

    @Test
    void completesMissionThroughAllActivities() {
        try (TestWorkflowEnvironment testEnvironment = TestWorkflowEnvironment.newInstance()) {
            Worker worker = testEnvironment.newWorker(TASK_QUEUE);
            worker.registerWorkflowImplementationTypes(DroneMissionWorkflowImpl.class);
            worker.registerActivitiesImplementations(new DroneActivitiesImpl());
            testEnvironment.start();

            DroneMissionWorkflow workflow = newWorkflowStub(testEnvironment.getWorkflowClient());
            String result = workflow.executeMission("DRONE-TEST");

            assertThat(result).contains("mission completed successfully");
        }
    }

    @Test
    void abortSignalReturnsDroneToBase() {
        try (TestWorkflowEnvironment testEnvironment = TestWorkflowEnvironment.newInstance()) {
            Worker worker = testEnvironment.newWorker(TASK_QUEUE);
            worker.registerWorkflowImplementationTypes(DroneMissionWorkflowImpl.class);
            worker.registerActivitiesImplementations(new DroneActivitiesImpl());
            testEnvironment.start();

            DroneMissionWorkflow workflow = newWorkflowStub(testEnvironment.getWorkflowClient());
            WorkflowClient.start(workflow::executeMission, "DRONE-ABORT-TEST");
            workflow.abortMission();
            String result = WorkflowStub.fromTyped(workflow).getResult(String.class);

            assertThat(result).contains("mission aborted");
            assertThat(result).contains("Return to base initiated");
        }
    }

    private DroneMissionWorkflow newWorkflowStub(WorkflowClient client) {
        return client.newWorkflowStub(
                DroneMissionWorkflow.class,
                WorkflowOptions.newBuilder().setTaskQueue(TASK_QUEUE).build()
        );
    }
}
