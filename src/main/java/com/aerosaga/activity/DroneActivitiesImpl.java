package com.aerosaga.activity;

public class DroneActivitiesImpl implements DroneActivities {

    @Override
    public void takeoff(String droneId) {
        System.out.println(droneId + " is taking off...");
    }

    @Override
    public void navigate(String droneId) {
        System.out.println(droneId + " is navigating to destination...");
    }

    @Override
    public void dropPackage(String droneId) {
        System.out.println(droneId + " has delivered the package!");
    }

    @Override
    public void returnToBase(String droneId) {
        System.out.println(droneId + " is returning to base...");
    }
}