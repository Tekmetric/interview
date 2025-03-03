import { render } from "@testing-library/react";
import { describe, expect } from "vitest";
import { ThemeProvider, createTheme } from "@mui/material/styles";
import Superscript from "./superscript";
import "@testing-library/jest-dom";

const theme = createTheme();

describe("Superscript", () => {
  test("Renders correctly with the correct styles", () => {
    const { getByText } = render(
      <ThemeProvider theme={theme}>
        <Superscript>AI</Superscript>
      </ThemeProvider>
    );

    const element = getByText("AI");
    expect(element).toBeInTheDocument();
    expect(element).toHaveStyle({
      color: theme.palette.success.main,
      fontSize: "0.6em",
      marginLeft: "0.2em",
      fontWeight: "bold",
    });
  });
});
