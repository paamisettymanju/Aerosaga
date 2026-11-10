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

## Requirements

- Java 21
- Node.js and npm
- Temporal CLI

## Run locally (Windows PowerShell)

Open three terminals from the project root.

1. Start Temporal:

   ```powershell
   temporal server start-dev
   ```

   The Temporal UI is at `http://localhost:8233`.

2. Start the backend:

   ```powershell
   cd .\backend\backend
   .\mvnw.cmd spring-boot:run
   ```

   The API listens on `http://localhost:8080`. The backend starts its Temporal worker automatically. Start Temporal before the backend.

3. Start the frontend:

   ```powershell
   cd .\
   npm install
   npm run dev
   ```

   Open the Vite URL printed in the terminal (normally `http://localhost:5173`). In development, Vite proxies `/api` requests to the backend.

### Configuration

The backend defaults to the local Temporal development server. Override these environment variables when using a different Temporal instance:

```powershell
$env:TEMPORAL_ADDRESS = "temporal.example:7233"
$env:TEMPORAL_NAMESPACE = "default"
```

For a frontend deployment where requests should not use Vite's development proxy, set `VITE_API_BASE_URL` to the backend's base URL before building.

## Mission controls

- Enter a drone ID using up to 40 letters, numbers, hyphens, or underscores.
- Start the mission and monitor its current phase; active missions are refreshed automatically.
- Abort an active mission to return the drone to base.
- Select one of the ten recent missions to resume monitoring it after a page refresh.

Mission progression is simulated; each flight phase waits up to two seconds so an abort request can be handled between stages. The workflow performs takeoff, navigation, package delivery, and return-to-base activities.

## API

- `POST /api/missions?droneId=DRONE-01` starts a mission and returns its workflow ID.
- `GET /api/missions/{workflowId}/status` returns the current phase and Temporal execution status.
- `POST /api/missions/{workflowId}/abort` requests an abort, or reports the final state if the mission has already finished.

## Tests and production build

Run backend tests, including in-process Temporal workflow completion and abort tests:

```powershell
cd .\backend\backend
.\mvnw.cmd test
```

Run the frontend lint and production build from the project root:

```powershell
npm run lint
npm run build
```

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
