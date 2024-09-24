import React from "react";
import { describe, expect, test } from "vitest";
import { render, screen} from "@testing-library/react";
import AddPanda from "../routes/panda/AddPanda";
import { BrowserRouter } from "react-router-dom";

describe("Add panda", () => {
  test("renders", () => {
    render(<BrowserRouter><AddPanda /></BrowserRouter>);
    expect(screen.getByText("Add panda")).toBeDefined();
  });

  test("renders the correct default options", () => {
    render(<BrowserRouter><AddPanda /></BrowserRouter>);

    expect(screen.getAllByRole("textbox", { value: undefined })).toHaveLength(2);

    expect(screen.queryByRole("checkbox", { name: "Has tracker", checked: true })).toBeNull();

    expect(screen.getAllByRole("radio", { name: "Himalayan", checked: true })).toHaveLength(1);
    expect(screen.queryByRole("radio", { name: "Chinese", checked: true })).toBeNull();
  });
});
