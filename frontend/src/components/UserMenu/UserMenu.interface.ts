import { IDrawerProps } from "../../types/IDrawerProps";
import { Themes } from "../../types/Theme";

export interface IUserMenuProps extends IDrawerProps {
  theme: Themes;
  setTheme: (theme: Themes) => void;
}
