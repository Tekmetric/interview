import axios from "axios";
import type { SearchResultLocation } from "./types/search";
import type { CurrentConditionsForecast, DailyForecast, ForecastLocation, HourlyForecast } from "./types/forecast";

const baseUrl = 'https://dataservice.accuweather.com';
const apiKey = import.meta.env.VITE_API_KEY;

const headers = {
    'Authorization': `Bearer ${import.meta.env.VITE_API_KEY}`
};

export async function getLocations(query: string): Promise<SearchResultLocation[]> {
    const response = await axios.get(`${baseUrl}/locations/v1/search?q=${query}`, { headers });

    return response.data.slice(0, 5);
}

export async function getForecastLocation(locationKey: string): Promise<ForecastLocation> {
    const response = await axios.get(`${baseUrl}/locations/v1/${locationKey}`, { headers });

    return response.data;
}

export async function getCurrentConditions(locationKey: string): Promise<CurrentConditionsForecast> {
    const response = await axios.get(`${baseUrl}/currentconditions/v1/${locationKey}`, {
        headers,
        params: {
            details: true
        }
    });

    return response.data[0];
}

export async function getDailyForecast(locationKey: string): Promise<DailyForecast[]> {
    const response = await axios.get(`${baseUrl}/forecasts/v1/daily/5day/${locationKey}`, {
        headers,
        params: {
            details: true
        }
    });

    return response.data.DailyForecasts;
}

export async function getHourlyForecast(locationKey: string): Promise<HourlyForecast[]> {
    const response = await axios.get(`${baseUrl}/forecasts/v1/hourly/12hour/${locationKey}`, {
        headers,
        params: {
            details: true
        }
    });

    return response.data;
}