import { describe, it, expect } from "vitest";
import { render, screen } from "@testing-library/react";
import Current from "~/components/forecast/current";
import type { CurrentConditionsForecast } from "~/types/forecast";

const mockCurrentConditions: CurrentConditionsForecast = {
  WeatherIcon: 1,
  WeatherText: "Sunny",
  Temperature: {
    Imperial: { Value: 72, Unit: "F" },
    Metric: { Value: 22, Unit: "C" }
  },
  Wind: {
    Direction: { Localized: "NW" },
    Speed: {
      Imperial: { Value: 10, Unit: "mph" },
      Metric: { Value: 16, Unit: "km/h" }
    }
  },
  UVIndex: 5,
  UVIndexText: "High",
  DewPoint: {
    Imperial: { Value: 45, Unit: "F" },
    Metric: { Value: 7, Unit: "C" }
  },
  Visibility: {
    Imperial: { Value: 10, Unit: "mi" },
    Metric: { Value: 16, Unit: "km" }
  }
};

describe("Current Component", () => {
  it("should render current conditions data correctly", async () => {
    const { container } = render(<Current data={mockCurrentConditions} />);
    await screen.findByText("Sunny");
    expect(container).toMatchSnapshot();
  });
});
