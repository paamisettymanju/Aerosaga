package com.aerosaga.workflow;

import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface DroneMissionWorkflow {

    @WorkflowMethod
    String executeMission(String droneId);

    @SignalMethod
    void abortMission();

    @QueryMethod
    String getMissionStatus();
}
