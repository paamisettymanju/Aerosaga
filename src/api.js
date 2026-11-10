const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "";

export async function apiRequest(path, options) {
  let response;
  try {
    response = await fetch(`${API_BASE_URL}${path}`, options);
  } catch {
    throw new Error("Unable to connect to the AeroSaga backend. Check that it is running.");
  }

  const body = await response.text();
  if (!response.ok) {
    throw new Error(body.trim() || `Request failed (${response.status}).`);
  }

  return body;
}
