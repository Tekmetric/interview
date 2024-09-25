import React from "react";
import { describe, expect, test } from "vitest";
import { render, screen} from "@testing-library/react";
import App from "../App";

describe("App", () => {
  test("renders login", () => {
    render(<App />);
    expect(screen.getByRole("heading", { name: "Sign in" })).toBeDefined();
    expect(screen.getByText("Welcome user, please sign in to continue")).toBeDefined();

    expect(screen.queryByText("Monitor red panda sightings")).toBeNull();
    expect(screen.queryByText("Welcome to Red Panda Tracker!")).toBeNull();
    expect(screen.queryByText("An app for logging your red panda sightings and monitoring pandas wearing a GPS tracker.")).toBeNull();
  });
});
