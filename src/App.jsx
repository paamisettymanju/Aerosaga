import { useEffect, useState } from "react";
import { apiRequest } from "./api";
import "./App.css";

function App() {
  const [droneId, setDroneId] = useState(
      () => window.localStorage.getItem("aerosaga.droneId") || "DRONE-01",
  );
  const [workflowId, setWorkflowId] = useState(
      () => window.localStorage.getItem("aerosaga.workflowId") ?? "",
  );
  const [status, setStatus] = useState("Not Started");
  const [loading, setLoading] = useState(false);
  const [checking, setChecking] = useState(false);
  const [aborting, setAborting] = useState(false);
  const [message, setMessage] = useState("");

  useEffect(() => {
    window.localStorage.setItem("aerosaga.droneId", droneId);
  }, [droneId]);

  useEffect(() => {
    if (workflowId) {
      window.localStorage.setItem("aerosaga.workflowId", workflowId);
    } else {
      window.localStorage.removeItem("aerosaga.workflowId");
    }
  }, [workflowId]);

  useEffect(() => {
    if (!workflowId) return undefined;

    let cancelled = false;
    let timeoutId;
    const pollStatus = async () => {
      try {
        const result = await apiRequest(
            `/api/missions/${encodeURIComponent(workflowId)}/status`,
        );
        if (cancelled) return;

        setStatus(result);
        if (result.includes("WORKFLOW_EXECUTION_STATUS_RUNNING")) {
          timeoutId = window.setTimeout(pollStatus, 2000);
        }
      } catch (error) {
        if (cancelled) return;
        setMessage(error.message);
        timeoutId = window.setTimeout(pollStatus, 5000);
      }
    };

    pollStatus();
    return () => {
      cancelled = true;
      window.clearTimeout(timeoutId);
    };
  }, [workflowId]);

  const startMission = async (event) => {
    event.preventDefault();
    setLoading(true);
    setWorkflowId("");
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
    if (!workflowId || checking) return;

    setChecking(true);
    try {
      const result = await apiRequest(
          `/api/missions/${encodeURIComponent(workflowId)}/status`,
      );
      setStatus(result);
    } catch (error) {
      setMessage(error.message);
    } finally {
      setChecking(false);
    }
  };
  const abortMission = async () => {
    if (!workflowId) {
      setMessage("Please start a mission first!");
      return;
    }

    setAborting(true);
    try {
      const result = await apiRequest(
          `/api/missions/${encodeURIComponent(workflowId)}/abort`,
          { method: "POST" },
      );
      setMessage(result);
      await checkStatus();
    } catch (error) {
      setMessage(error.message);
    } finally {
      setAborting(false);
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
            <form onSubmit={startMission}>
              <label htmlFor="drone-id">Drone ID</label>
              <input
                  id="drone-id"
                  name="droneId"
                  value={droneId}
                  onChange={(e) => setDroneId(e.target.value)}
                  placeholder="Enter drone ID"
                  autoComplete="off"
                  required
                  maxLength={40}
                  pattern="[A-Za-z0-9][A-Za-z0-9_-]{0,39}"
                  title="Use 1–40 letters, numbers, hyphens, or underscores; start with a letter or number."
              />
              <button type="submit" disabled={loading || checking || aborting || !droneId.trim()}>
                {loading ? "Starting..." : "Start Mission"}
              </button>
            </form>

            <button
                type="button"
                className="secondary"
                onClick={checkStatus}
                disabled={!workflowId || checking || aborting}
            >
              {checking ? "Checking..." : "Check Mission Status"}
            </button>
            <button
                type="button"
                className="secondary"
                onClick={abortMission}
                disabled={!workflowId || checking || aborting}
            >
              {aborting ? "Aborting..." : "Abort Mission"}
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