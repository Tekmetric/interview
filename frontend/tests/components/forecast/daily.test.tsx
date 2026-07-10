import { describe, it, expect } from "vitest";
import { render, screen } from "@testing-library/react";
import Daily from "~/components/forecast/daily";
import type { DailyForecast } from "~/types/forecast";

const mockDailyForecast: DailyForecast[] = [
  {
    Date: "2024-02-12T12:00:00",
    Temperature: {
      Maximum: { Value: 75, Unit: "F" },
      Minimum: { Value: 55, Unit: "F" }
    },
    Day: {
      Icon: 1,
      IconPhrase: "Sunny",
      PrecipitationProbability: 10,
      LongPhrase: "Sunny throughout the day",
      TotalLiquid: { Value: 0, Unit: "in" },
      Wind: {
        Direction: { Localized: "NW" },
        Speed: { Value: 10, Unit: "mph" }
      }
    },
    Night: {
      Icon: 33,
      IconPhrase: "Clear",
      PrecipitationProbability: 5,
      LongPhrase: "Clear night",
      TotalLiquid: { Value: 0, Unit: "in" },
      Wind: {
        Direction: { Localized: "NW" },
        Speed: { Value: 5, Unit: "mph" }
      }
    },
    Sun: { Rise: "2024-02-12T07:00:00", Set: "2024-02-12T18:00:00" },
    Moon: { Rise: "2024-02-12T20:00:00", Set: "2024-02-13T08:00:00" }
  }
];



describe("Daily Component", () => {
  it("should render daily forecast data correctly", async () => {
    const { container } = render(<Daily data={mockDailyForecast} />);
    await screen.findByText("Mon, 2/12 (Day)");
    expect(container).toMatchSnapshot();
  });
});
