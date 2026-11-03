package com.aerosaga.workflow;

import com.aerosaga.activity.DroneActivities;
import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;

import java.time.Duration;

public class DroneMissionWorkflowImpl implements DroneMissionWorkflow {

    private final DroneActivities activities =
            Workflow.newActivityStub(
                    DroneActivities.class,
                    ActivityOptions.newBuilder()
                            .setStartToCloseTimeout(Duration.ofMinutes(2))
                            .build()
            );

    // Stores the emergency abort request
    private boolean abortRequested = false;

    // Receives the emergency abort signal
    @Override
    public void abortMission() {
        abortRequested = true;
    }

    // Executes the drone mission
    @Override
    public String executeMission(String droneId) {

        System.out.println("Mission started for " + droneId);

        // Step 1: Takeoff
        activities.takeoff(droneId);

        if (abortRequested) {
            return returnToBaseAfterAbort(droneId);
        }

        // Step 2: Navigate
        activities.navigate(droneId);

        if (abortRequested) {
            return returnToBaseAfterAbort(droneId);
        }

        // Step 3: Drop package
        activities.dropPackage(droneId);
        // Check abort request after package delivery
        if (abortRequested) {
            return returnToBaseAfterAbort(droneId);
        }

        // Step 4: Return to base
        activities.returnToBase(droneId);

        return droneId + " mission completed successfully!";
    }

    // Compensation action after abort
    private String returnToBaseAfterAbort(String droneId) {

        activities.returnToBase(droneId);

        return droneId + " mission aborted. Return to base requested.";
    }
}