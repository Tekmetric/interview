import { describe, it, expect } from "vitest";
import { render, screen } from "@testing-library/react";
import Hourly from "~/components/forecast/hourly";
import type { HourlyForecast } from "~/types/forecast";


const mockHourlyForecast: HourlyForecast[] = [
  {
    DateTime: "2024-02-12T10:00:00",
    WeatherIcon: 1,
    IconPhrase: "Sunny",
    PrecipitationProbability: 10,
    Temperature: { Value: 72, Unit: "°F" },
    Wind: {
      Direction: { Localized: "NW" },
      Speed: { Value: 10, Unit: "mph" }
    },
    UVIndex: 5,
    UVIndexText: "High",
    DewPoint: { Value: 45, Unit: "°F" },
    Visibility: { Value: 10, Unit: "mi" }
  },
  {
    DateTime: "2024-02-12T11:00:00",
    WeatherIcon: 2,
    IconPhrase: "Partly Cloudy",
    PrecipitationProbability: 15,
    Temperature: { Value: 73, Unit: "°F" },
    Wind: {
      Direction: { Localized: "NW" },
      Speed: { Value: 12, Unit: "mph" }
    },
    UVIndex: 6,
    UVIndexText: "High",
    DewPoint: { Value: 46, Unit: "°F" },
    Visibility: { Value: 10, Unit: "mi" }
  }
];



describe("Hourly Component", () => {
  it("should render hourly forecast data correctly", async () => {
    const { container } = render(<Hourly data={mockHourlyForecast} />);
    await screen.findByText("Sunny");
    expect(container).toMatchSnapshot();
  });
});
