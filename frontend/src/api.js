// import { API_KEY } from "./key";

// I have a real API key I can share if this one gets rate-limited.
const API_KEY = "DEMO_KEY";
const API_URL = `https://api.nasa.gov/neo/rest/v1/feed?api_key=${API_KEY}`;

export async function fetchNEOs(date) {
  try {
    // include start and end date to limit to one day, otherwise it defaults to 7 days of data.
    const results = await fetch(`${API_URL}&start_date=${date}&end_date=${date}`);
    const resultsJson = await results.json();

    if (resultsJson.error) {
      throw new Error(resultsJson.error.message);
    }

    return resultsJson.near_earth_objects[date];
  } catch (e) {
    console.error(e);
    return e.message;
  }
}
