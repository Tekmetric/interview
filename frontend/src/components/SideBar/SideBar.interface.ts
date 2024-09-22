import { MenuTabs } from "../../constants/menu.constants";

export interface ISideBarMenuItem {
  key: MenuTabs,
  icon: React.ReactNode,
  text: string,
  href: string
}
