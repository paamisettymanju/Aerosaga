import { useState } from "react";
import { apiRequest } from "./api";
import "./App.css";

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
      const result = await apiRequest(
          `/api/missions?droneId=${encodeURIComponent(droneId)}`,
          { method: "POST" },
      );
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
      const result = await apiRequest(
          `/api/missions/${encodeURIComponent(workflowId)}/status`,
      );
      setStatus(result);
    } catch (error) {
      setMessage(error.message);
    }
  };
  const abortMission = async () => {
    if (!workflowId) {
      setMessage("Please start a mission first!");
      return;
    }

    try {
      const result = await apiRequest(
          `/api/missions/${encodeURIComponent(workflowId)}/abort`,
          { method: "POST" },
      );
      setMessage(result);
      await checkStatus();
    } catch (error) {
      setMessage(error.message);
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