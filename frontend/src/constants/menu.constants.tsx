import HomeIcon from '@mui/icons-material/Home';
import GpsIcon from '@mui/icons-material/GpsFixed';
import PetsIcon from '@mui/icons-material/Pets';
import LocationIcon from '@mui/icons-material/LocationOn';
import { ISideBarMenuItem } from '../components/SideBar/SideBar.interface';
import { Routes } from './routes.constants';

export enum MenuTabs {
  Home, 
  Sightings,
  Pandas,
  Locations
}

export const homeMenuItem: ISideBarMenuItem = {
  key: MenuTabs.Home,
  icon: <HomeIcon />,
  text: "Home",
  href: Routes.home,
};

export const listMenuItem: ISideBarMenuItem = {
  key: MenuTabs.Sightings,
  icon: <GpsIcon />,
  text: "Sightings",
  href: Routes.sightings
};


export const menuItems: ISideBarMenuItem[] = [
  {
    ...homeMenuItem
  },
  {
    ...listMenuItem
  },
  {
    key: MenuTabs.Pandas,
    icon: <PetsIcon />,
    text: "Pandas",
    href: Routes.pandas
  },
  {
    key: MenuTabs.Locations,
    icon: <LocationIcon />,
    text: "Locations",
    href: Routes.locations
  }
];
