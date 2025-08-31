import React from "react";
import { render } from "react-dom";
import { act } from "react-dom/test-utils";
import { useRockets } from "./useRockets";
import mockResult from "./mockResult.json";

global.fetch = jest.fn();

function TestComponent() {
  const hookResult = useRockets();
  return (
    <div
      data-testid="rockets-result"
      data-rockets={JSON.stringify(hookResult.rockets)}
      data-loading={hookResult.loading}
      data-error={hookResult.error}
      data-max-height={hookResult.getMaxRocketHeight()}
      data-max-diameter={hookResult.getMaxRocketDiameter()}
    />
  );
}

describe("useRockets", () => {
  let container;

  beforeEach(() => {
    container = document.createElement("div");
    document.body.appendChild(container);
  });

  afterEach(() => {
    document.body.removeChild(container);
    container = null;
  });

  const renderComponent = async (component) => {
    // Act is deprecated:
    // I'd update React to 18+, rip out react-scripts (replace with vite) and use the new testing library to fix this.
    await act(async () => {
      render(component, container);
    });
  };

  it("should fetch rockets and calculate values, max height and max diameter, on mount", async () => {
    fetch.mockResolvedValueOnce({
      ok: true,
      json: async () => mockResult,
    });

    await renderComponent(<TestComponent />);

    const resultElement = container.querySelector(
      '[data-testid="rockets-result"]'
    );
    const rockets = JSON.parse(resultElement.getAttribute("data-rockets"));
    const loading = resultElement.getAttribute("data-loading");
    const error = resultElement.getAttribute("data-error");
    const getMaxRocketHeight = resultElement.getAttribute("data-max-height");
    const getMaxRocketDiameter =
      resultElement.getAttribute("data-max-diameter");

    expect(loading).toBe("false");
    expect(rockets).toEqual(mockResult);
    expect(error).toBe(null);
    expect(getMaxRocketHeight).toBe("118");
    expect(getMaxRocketDiameter).toBe("12.2");
  });

  it("Should show an error message if the fetch fails", async () => {
    fetch.mockRejectedValueOnce(new Error("Failed to fetch rockets"));
    await renderComponent(<TestComponent />);
    const resultElement = container.querySelector(
      '[data-testid="rockets-result"]'
    );
    const error = resultElement.getAttribute("data-error");
    expect(error).toBe("Failed to fetch rockets");
  });
});
