import React from "react";
import { describe, expect, test } from "vitest";
import { render, screen} from "@testing-library/react";
import App from "../App";

describe("Authenticated App", () => {
  test("renders", () => {
    render(<App />);
    expect(screen.getByText("Monitor red panda sightings")).toBeDefined();
    expect(screen.getByText("Welcome to Red Panda Tracker!")).toBeDefined();
    expect(screen.getByText("An app for logging your red panda sightings and monitoring pandas wearing a GPS tracker.")).toBeDefined();
  });
});
