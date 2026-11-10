# AeroSaga: Temporal.io Autonomous Drone Mission Control

## Project Overview

AeroSaga is a simulated autonomous drone mission control system built with Java, Spring Boot, Temporal.io, and React. It demonstrates workflow orchestration for a drone mission lifecycle, including start, execution, abort handling, and status monitoring.

## Features

- Start a drone mission
- Simulate drone takeoff and flight path
- Simulate package delivery
- Return the drone to base
- Check mission status
- Abort a mission mid-flight

## Technology Stack

- Java 21
- Spring Boot
- Temporal.io
- React + Vite
- Maven

## Run the project

1. Start a Temporal dev server locally on `localhost:7233`.
2. Start the backend:
   - `cd backend/backend`
   - `./mvnw spring-boot:run`
3. Start the frontend:
   - `cd ..`
   - `npm install`
   - `npm run dev`
4. Open the Vite app in the browser and begin managing missions.

## Mission workflow behavior

The Temporal workflow executes the following order:

1. Takeoff
2. Navigate
3. Deliver package
4. Return to base
5. Complete mission

If an abort signal is received at any point, the drone immediately returns to base and the mission is marked as aborted.

## Project Purpose

This project demonstrates workflow orchestration and autonomous mission simulation using Temporal.io in a realistic drone-control workflow.

## Developed By

B.Tech Electronics and Communication Engineering Student
