const API_URL = "https://api.nasa.gov/neo/rest/v1/feed?api_key=DEMO_KEY";

export async function fetchNEOs(date) {
  try {
    // include start and end date to limit to one day, otherwise it defaults to 7 days of data.
    const results = await fetch(`${API_URL}&start_date=${date}&end_date=${date}`);
    const resultsJson = await results.json();

    return resultsJson.near_earth_objects[date];
  } catch (e) {
    console.error(e);
    return e.message;
  }
}
