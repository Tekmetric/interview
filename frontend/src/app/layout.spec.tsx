import { render, screen } from "@testing-library/react";
import "@testing-library/jest-dom";
import RootLayout from "./layout";

// Mock the Roboto function from next/font/google
vi.mock("next/font/google", () => ({
  Roboto: vi.fn().mockReturnValue({
    variable: "--font-roboto",
  }),
}));

describe("RootLayout", () => {
  test("Render children correctly", () => {
    render(
      <RootLayout>
        <div data-testid="child-element">Child Element</div>
      </RootLayout>
    );

    expect(screen.getByTestId("child-element")).toBeInTheDocument();
    expect(screen.getByText("Child Element")).toBeInTheDocument();
  });

  test("Apply the correct HTML structure", () => {
    render(
      <RootLayout>
        <div data-testid="child-element">Child Element</div>
      </RootLayout>
    );

    const htmlElement = document.querySelector("html");
    const bodyElement = document.querySelector("body");

    expect(htmlElement).toHaveAttribute("lang", "en");
    expect(bodyElement).toHaveClass("antialiased");
    expect(bodyElement).toHaveClass("--font-roboto");
  });

  test("Include ThemeProvider and CssBaseline", () => {
    render(
      <RootLayout>
        <div data-testid="child-element">Child Element</div>
      </RootLayout>
    );

    // Check if ThemeProvider is applied
    const themeProvider = screen.getByTestId("child-element").closest("div");
    expect(themeProvider).toBeInTheDocument();

    // Check if CssBaseline is applied
    const cssBaseline = document.querySelector("style[data-emotion]");
    expect(cssBaseline).toBeInTheDocument();
  });
});
