export interface Measurement {
    Value: number;
    Unit: string;
}

export interface MeasurementFormats {
    Metric: Measurement;
    Imperial: Measurement;
}

export interface DailyPeriodData {
    Icon: number;
    IconPhrase: string;
    LongPhrase: string;
    PrecipitationProbability: number;
    TotalLiquid: Measurement;
    Wind: {
        Direction: {
            Localized: string;
        };
        Speed: Measurement;
    };
}

export interface CelestialData {
    Rise: string;
    Set: string;
}

export interface ForecastLocation {
    LocalizedName: string;
    AdministrativeArea: {
        LocalizedName: string;
    }
}

export interface CurrentConditionsForecast {
    WeatherText: string;
    WeatherIcon: number;
    Temperature: MeasurementFormats;
    DewPoint: MeasurementFormats;
    Wind: {
        Direction: {
            Localized: string;
        };
        Speed: MeasurementFormats;
    };
    UVIndex: number;
    UVIndexText: string;
    Visibility: MeasurementFormats;
}

export interface DailyForecast {
    Date: string;
    Temperature: {
        Minimum: Measurement;
        Maximum: Measurement;
    };
    Day: DailyPeriodData;
    Night: DailyPeriodData;
    Sun: CelestialData;
    Moon: CelestialData;
}

export interface HourlyForecast {
    DateTime: string;
    WeatherIcon: number;
    IconPhrase: string;
    Temperature: Measurement;
    Wind: {
        Direction: {
            Localized: string;
        };
        Speed: Measurement;
    };
    DewPoint: Measurement;
    UVIndex: number;
    UVIndexText: string;
    PrecipitationProbability: number;
    Visibility: Measurement;
}

export interface ForecastDetails {
    current: CurrentConditionsForecast;
    daily: DailyForecast[];
    hourly: HourlyForecast[];
}