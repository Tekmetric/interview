import type { RealTimeRailPredictionsResponse } from '@customTypes/api/wmata';
import { queryOptions } from '@tanstack/react-query';

// https://developer.wmata.com/api-details#api=547636a6f9182302184cda78&operation=547636a6f918230da855363f
export async function getMetroArrivalTimes(stationCodes: string = 'All'): Promise<RealTimeRailPredictionsResponse> {
  const headers = new Headers();
  headers.append('api_key', import.meta.env.VITE_WMATA_API_KEY);
  const response = await fetch(
    `https://api.wmata.com/StationPrediction.svc/json/GetPrediction/${ stationCodes }`,
    { headers }
  );
  if (!response.ok) {
    throw new Error("Couldn't get data from WMATA API");
  }
  return await response.json();
}

export const metroArrivalTimesOptions = (stationCodes: string) =>
  queryOptions({
    queryKey: ['stationCodes', stationCodes],
    queryFn: () => getMetroArrivalTimes(stationCodes)
  });
