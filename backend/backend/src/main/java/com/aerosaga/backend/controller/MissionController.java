package com.aerosaga.backend.controller;

import com.aerosaga.backend.service.TemporalMissionService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
@RestController
@RequestMapping("/api/missions")
public class MissionController {

    private final TemporalMissionService temporalMissionService;

    public MissionController(TemporalMissionService temporalMissionService) {
        this.temporalMissionService = temporalMissionService;
    }

    @PostMapping
    public String startMission(
            @RequestParam(defaultValue = "DRONE-01") String droneId) {

        String workflowId = temporalMissionService.startMission(droneId);

        return "Drone mission started! Workflow ID: " + workflowId;
    }

    @GetMapping("/{workflowId}/status")
    public ResponseEntity<String> getMissionStatus(
            @PathVariable String workflowId) {

        String status = temporalMissionService.getMissionStatus(workflowId);

        return ResponseEntity.ok(status);
    }
    @PostMapping("/{workflowId}/abort")
    public ResponseEntity<String> abortMission(
            @PathVariable String workflowId) {

        String result = temporalMissionService.abortMission(workflowId);

        return ResponseEntity.ok(result);
    }
}