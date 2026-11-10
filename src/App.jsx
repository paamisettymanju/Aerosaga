import { useState } from "react";
import "./App.css";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "";

function App() {
  const [droneId, setDroneId] = useState("DRONE-01");
  const [workflowId, setWorkflowId] = useState("");
  const [status, setStatus] = useState("Not Started");
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");

  const startMission = async () => {
    setLoading(true);
    setMessage("");
    setStatus("Starting...");

    try {
      const response = await fetch(
          `${API_BASE_URL}/api/missions?droneId=${encodeURIComponent(droneId)}`,
          { method: "POST" }
      );

      if (!response.ok) throw new Error("Unable to start mission");

      const result = await response.text();
      const id = result.split("Workflow ID: ")[1]?.trim();

      if (!id) throw new Error(result);

      setWorkflowId(id);
      setStatus("RUNNING");
      setMessage("Mission started successfully!");
    } catch (error) {
      setStatus("ERROR");
      setMessage(error.message);
    } finally {
      setLoading(false);
    }
  };

  const checkStatus = async () => {
    if (!workflowId) return;

    try {
      const response = await fetch(
          `${API_BASE_URL}/api/missions/${encodeURIComponent(workflowId)}/status`
      );

      const result = await response.text();
      setStatus(result);
    } catch {
      setMessage("Unable to connect to backend.");
    }
  };
  const abortMission = async () => {
    if (!workflowId) {
      setMessage("Please start a mission first!");
      return;
    }

    try {
      const response = await fetch(
          `${API_BASE_URL}/api/missions/${encodeURIComponent(workflowId)}/abort`,
          {
            method: "POST"
          }
      );

      const result = await response.text();
      setMessage(result);
      await checkStatus();
    } catch{
      setMessage("Failed to abort mission.");
    }
  };

  return (
      <div className="dashboard">
        <header className="header">
          <h1>🚁 AeroSaga</h1>
          <p>Autonomous Drone Mission Control</p>
        </header>

        <main className="main">
          <section className="panel">
            <h2>Mission Control</h2>
            <label htmlFor="drone-id">Drone ID</label>

            <input
                id="drone-id"
                name="droneId"
                value={droneId}
                onChange={(e) => setDroneId(e.target.value)}
                placeholder="Enter drone ID"
            />

            <button type="button" onClick={startMission} disabled={loading || !droneId.trim()}>
              {loading ? "Starting..." : "Start Mission"}
            </button>

            <button
                type="button"
                className="secondary"
                onClick={checkStatus}
                disabled={!workflowId}
            >
              Check Mission Status
            </button>
            <button
                type="button"
                className="secondary"
                onClick={abortMission}
                disabled={!workflowId}
            >
              Abort Mission
            </button>

            <p className="message" role="status" aria-live="polite">{message}</p>
          </section>

          <section className="panel">
            <h2>Mission Details</h2>
            <p><strong>Drone:</strong> {droneId}</p>
            <p><strong>Workflow ID:</strong></p>
            <p className="workflow" aria-label="Workflow ID">{workflowId || "No mission started yet"}</p>
            <p><strong>Status:</strong> <span className="status" role="status" aria-live="polite">{status}</span></p>
          </section>
        </main>
      </div>
  );
}

export default App;