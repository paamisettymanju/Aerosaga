package com.aerosaga.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DroneController {

    @GetMapping("/api/status")
    public String getStatus() {
        return "AeroSaga Backend is Running!";
    }
}