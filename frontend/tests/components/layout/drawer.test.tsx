import { describe, it, expect } from "vitest";
import { render, screen, fireEvent } from "@testing-library/react";
import Drawer from "~/components/layout/drawer";

describe("Drawer Component", () => {
  it("should render collapsed state correctly", () => {
    const { container } = render(
      <Drawer
        main={<div>Main Content</div>}
        expandedContent={<div>Expanded Content</div>}
      />
    );
    expect(container).toMatchSnapshot();
  });

  it("should render expanded state correctly", () => {
    const { container } = render(
      <Drawer
        main={<div>Main Content</div>}
        expandedContent={<div>Expanded Content</div>}
      />
    );

    const drawerButton = container.querySelector("div[class*='cursor-pointer']");
    fireEvent.click(drawerButton!);

    expect(container).toMatchSnapshot();
  });

  it("should toggle between collapsed and expanded", () => {
    render(
      <Drawer
        main={<div>Main Content</div>}
        expandedContent={<div>Expanded Content</div>}
      />
    );

    expect(screen.queryByText("Expanded Content")).toBeNull();

    const drawerButton = screen.getByText("Main Content").closest("div[class*='cursor-pointer']");
    fireEvent.click(drawerButton!);

    expect(screen.getByText("Expanded Content")).toBeTruthy();
  });
});
