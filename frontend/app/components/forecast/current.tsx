import { Fragment } from "react";
import { getWeatherIconUrl } from "~/utils/forecast";
import KeyValueItem from "../layout/key-value-item";
import type { CurrentConditionsForecast } from "~/types/forecast";

export default function Current({ data }: { data: CurrentConditionsForecast }) {
    return (
        <Fragment>
            {
                data ? (
                    <div className="flex md:flex-row flex-col md:space-between space-y-4">
                        <div className="md:w-1/2 w-full flex flex-col space-y-2">
                            <div className="flex space-x-4 items-center">
                                <img src={getWeatherIconUrl(data.WeatherIcon)} />
                                <div className="font-bold text-5xl">
                                    {data.Temperature.Imperial.Value}° {data.Temperature.Imperial.Unit}
                                </div>
                            </div>
                            <p className="text-lg">{data.WeatherText}</p>
                        </div>
                        <div className="md:w-1/2 w-full flex flex-col justify-evenly divide-y divide-gray-300 border border-gray-300 rounded-md">
                            <KeyValueItem label={'Wind'} value={`${data.Wind.Direction.Localized} ${data.Wind.Speed.Imperial.Value} ${data.Wind.Speed.Imperial.Unit}`} />
                            <KeyValueItem label={'UV Index'} value={`${data.UVIndex} (${data.UVIndexText})`} />
                            <KeyValueItem label={'Dew Point'} value={`${data.DewPoint.Imperial.Value}° ${data.DewPoint.Imperial.Unit}`} />
                            <KeyValueItem label={'Visibility'} value={`${data.Visibility.Imperial.Value} ${data.Visibility.Imperial.Unit}`} />
                        </div>
                    </div>
                ) : null
            }
        </Fragment>
    );
}