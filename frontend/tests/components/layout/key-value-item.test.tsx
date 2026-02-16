import { describe, it, expect } from "vitest";
import KeyValueItem from "~/components/layout/key-value-item";
import { render } from "@testing-library/react";

describe("KeyValueItem Component", () => {
  it("should render correctly", () => {
    const { container } = render(<KeyValueItem label="Test Key" value="Test Value" />);
    expect(container).toMatchSnapshot();
  });
});
