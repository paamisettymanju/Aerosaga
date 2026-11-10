package com.aerosaga.workflow;

import com.aerosaga.activity.DroneActivities;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;

import java.time.Duration;

public class DroneMissionWorkflowImpl implements DroneMissionWorkflow {

    private final DroneActivities activities = Workflow.newActivityStub(
            DroneActivities.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofMinutes(2))
                    .setRetryOptions(RetryOptions.newBuilder()
                            .setInitialInterval(Duration.ofSeconds(1))
                            .setMaximumInterval(Duration.ofSeconds(10))
                            .setBackoffCoefficient(2.0)
                            .setMaximumAttempts(3)
                            .build())
                    .build()
    );

    private boolean abortRequested = false;
    private String currentPhase = "INITIALIZED";

    @Override
    public void abortMission() {
        abortRequested = true;
        currentPhase = "ABORT_REQUESTED";
    }

    @Override
    public String getMissionStatus() {
        return currentPhase;
    }

    @Override
    public String executeMission(String droneId) {
        System.out.println("Mission started for " + droneId);

        currentPhase = "TAKEOFF";
        waitForAbortOrSimulationStep();
        if (abortRequested) {
            return finishAbortedMission(droneId, "takeoff");
        }
        activities.takeoff(droneId);
        if (abortRequested) {
            return finishAbortedMission(droneId, "takeoff");
        }

        currentPhase = "NAVIGATING";
        waitForAbortOrSimulationStep();
        if (abortRequested) {
            return finishAbortedMission(droneId, "navigation");
        }
        activities.navigate(droneId);
        if (abortRequested) {
            return finishAbortedMission(droneId, "navigation");
        }

        currentPhase = "DELIVERING_PACKAGE";
        waitForAbortOrSimulationStep();
        if (abortRequested) {
            return finishAbortedMission(droneId, "package delivery");
        }
        activities.dropPackage(droneId);
        if (abortRequested) {
            return finishAbortedMission(droneId, "package delivery");
        }

        currentPhase = "RETURNING_TO_BASE";
        waitForAbortOrSimulationStep();
        if (abortRequested) {
            return finishAbortedMission(droneId, "return to base");
        }
        activities.returnToBase(droneId);
        currentPhase = "COMPLETED";

        return droneId + " mission completed successfully!";
    }

    private String finishAbortedMission(String droneId, String stage) {
        currentPhase = "ABORTED";
        activities.returnToBase(droneId);
        return droneId + " mission aborted during " + stage + ". Return to base initiated.";
    }

    private void waitForAbortOrSimulationStep() {
        Workflow.await(java.time.Duration.ofSeconds(2), () -> abortRequested);
    }
}
