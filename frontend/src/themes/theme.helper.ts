import { Themes } from "../types/Theme";
import { darkTheme } from "./dark";
import { lightTheme } from "./light";
import { Theme } from "@mui/material";

export function getTheme(themeId: Themes): Theme {
  switch(themeId) {
    case Themes.Light: return lightTheme;
    case Themes.Dark: return darkTheme;
    default: return lightTheme;
  }
}
