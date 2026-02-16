import { formatTime, getWeatherIconUrl } from "~/utils/forecast";
import Drawer from "../layout/drawer";
import { Fragment } from "react";
import KeyValueItem from "../layout/key-value-item";
import type { HourlyForecast } from "~/types/forecast";

export default function Hourly({ data }: { data: HourlyForecast[] }) {
    return (
        <Fragment>
            {
                data.length ? (
                    <div className="flex flex-col space-y-4">
                        {data.map((hour, index) => (
                            <Drawer
                                key={index}
                                main={
                                    <div className="flex flex-col space-y-2">
                                        <div className="flex items-center justify-between">
                                            <div className="flex items-center md:space-x-4 space-x-2">
                                                <div className="font-bold md:text-lg">{formatTime(hour.DateTime)}</div>
                                                <img src={getWeatherIconUrl(hour.WeatherIcon)} />
                                                <div className="font-bold md:text-3xl text-xl">{hour.Temperature.Value}° {hour.Temperature.Unit}</div>
                                                <p className="md:text-md text-sm">{hour.IconPhrase}</p>
                                            </div>
                                            <div className="text-sm flex items-center space-x-1">
                                                <span className="material-icons-outlined text-gray-500">water_drop</span><span>{hour.PrecipitationProbability}%</span>
                                            </div>
                                        </div>
                                    </div>
                                }
                                expandedContent={
                                    <div className="lg:w-1/2 w-full flex flex-col justify-evenly divide-y divide-gray-300">
                                        <KeyValueItem label="Wind" value={`${hour.Wind.Direction.Localized} ${hour.Wind.Speed.Value} ${hour.Wind.Speed.Unit}`} />
                                        <KeyValueItem label="UV Index" value={`${hour.UVIndex} (${hour.UVIndexText})`} />
                                        <KeyValueItem label="Dew Point" value={`${hour.DewPoint.Value}° ${hour.DewPoint.Unit}`} />
                                        <KeyValueItem label="Visibility" value={`${hour.Visibility.Value} ${hour.Visibility.Unit}`} />
                                    </div>
                                }
                            />
                        ))}
                    </div>
                ) : null
            }
        </Fragment>
    );
}