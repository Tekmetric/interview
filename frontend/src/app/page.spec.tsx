import { render, screen } from "@testing-library/react";
import Page from "./page";
import "@testing-library/jest-dom";

test("Page", () => {
  render(<Page />);

  // Check for the heading
  expect(
    screen.getByRole("heading", { level: 1, name: "Portfolio Builder" })
  ).toBeDefined();

  // Check for the PortfolioBuilder component
  expect(screen.getByText("Portfolio Builder")).toBeInTheDocument();

  // Check for the footer
  expect(screen.getByText("Footer")).toBeInTheDocument();
});
