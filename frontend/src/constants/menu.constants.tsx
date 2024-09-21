import HomeIcon from '@mui/icons-material/Home';
import ListIcon from '@mui/icons-material/List';
import LocationIcon from '@mui/icons-material/LocationOn';
import { ISideBarMenuItem } from '../components/SideBar/SideBar.interface';

export enum MenuTabs {
  Home, 
  List,
  Locations
}

export const homeMenuItem: ISideBarMenuItem = {
  key: MenuTabs.Home,
  icon: <HomeIcon />,
  text: "Home",
  href: "/"
};

export const listMenuItem: ISideBarMenuItem = {
  key: MenuTabs.List,
  icon: <ListIcon />,
  text: "Entities",
  href: "/list"
};


export const menuItems: ISideBarMenuItem[] = [
  {
    ...homeMenuItem
  },
  {
    ...listMenuItem
  },
  {
    key: MenuTabs.Locations,
    icon: <LocationIcon />,
    text: "Locations",
    href: "/locations"
  }
];
