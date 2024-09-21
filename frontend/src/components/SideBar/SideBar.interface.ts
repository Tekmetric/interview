import { MenuTabs } from "../../constants/menu.constants";

export interface ISideBarProps {
  open: boolean;
  setOpen: (isOpen: boolean) => void;
}

export interface ISideBarMenuItem {
  key: MenuTabs,
  icon: React.ReactNode,
  text: string,
  href: string
}
