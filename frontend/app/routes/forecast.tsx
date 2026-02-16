import { getCurrentConditions, getDailyForecast, getForecastLocation, getHourlyForecast } from "~/api";
import { useEffect, useState } from "react";
import Tabs from "~/components/forecast/tabs";
import type { ForecastDetails, ForecastLocation } from "~/types/forecast";
import type { Route } from "./+types/forecast";

export default function Forecast({ params }: Route.ComponentProps) {
    const [forecastDetails, setForecastDetails] = useState<ForecastDetails | null>(null);
    const [forecastLocation, setForecastLocation] = useState<ForecastLocation | null>(null);
    const [isLoading, setIsLoading] = useState(false);
    const [hasError, setHasError] = useState(false);

    useEffect(() => {
        const fetchForecastDetails = async () => {
            try {
                setHasError(false);
                setIsLoading(true);
                const [location, current, daily, hourly] = await Promise.all([
                    getForecastLocation(params.locationId),
                    getCurrentConditions(params.locationId),
                    getDailyForecast(params.locationId),
                    getHourlyForecast(params.locationId)
                ]);
                setForecastLocation(location);
                setForecastDetails({
                    current,
                    daily,
                    hourly
                });
            } catch (error) {
                setHasError(true);
            } finally {
                setIsLoading(false);
            }
        }
        
        fetchForecastDetails();
    }, [params.locationId]);

    return (
        <div className="md:py-16 py-8 bg-white md:bg-transparent md:w-3/4 w-full h-[calc(100%-4rem)] min-h-[calc(100vh-4rem)] mx-auto">
            {isLoading ? <p className="text-center py-8 bg-white md:rounded-lg md:border md:border-gray-300">Loading forecast details...</p> : (
                forecastLocation && forecastDetails ? (
                    <div className="py-8 bg-white md:rounded-lg md:border md:border-gray-300">
                        <h2 className="text-4xl font-bold md:mx-8 mx-4 mb-4">
                            {`${forecastLocation.LocalizedName}, ${forecastLocation.AdministrativeArea.LocalizedName}`}
                        </h2>
                        <Tabs forecast={forecastDetails} />
                    </div>
                ) : hasError ? <p className="text-center py-8 bg-white md:rounded-lg md:border md:border-gray-300">Error loading forecast data. Please try again later.</p> : null
            )} 
        </div>
    );
}