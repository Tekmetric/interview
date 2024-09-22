import React from 'react';
import { useTheme } from '@mui/material/styles';
import Divider from '@mui/material/Divider';
import IconButton from '@mui/material/IconButton';
import ListItem from '@mui/material/ListItem';
import ListItemButton from '@mui/material/ListItemButton';
import ChevronRightIcon from '@mui/icons-material/ChevronRight';
import ChevronLeftIcon from '@mui/icons-material/ChevronLeft';
import { menuItems } from '../../constants/menu.constants';
import { useNavigate } from 'react-router-dom';
import RedPandaLogo from "../../assets/panda-logo.png";
import { Drawer, DrawerHeader, DrawerItems, ListItemIcon, ListItemText } from './Sidebar.style';
import { ISideBarMenuItem } from './SideBar.interface';
import { Typography } from '@mui/material';
import { IDrawerProps } from '../../types/IDrawerProps';

export default function SideBar({ open, setOpen }: IDrawerProps) {
  const theme = useTheme();

  const navigate = useNavigate();

  const handleDrawerOpen = () => {
    setOpen(true);
  };

  const handleDrawerClose = () => {
    setOpen(false);
  };

  const handleMenuItemSelection = (item: ISideBarMenuItem) => {
    navigate(item.href);
  }

  return (
    <Drawer variant="permanent" open={open} onMouseEnter={handleDrawerOpen} onMouseLeave={handleDrawerClose}>
      <DrawerHeader>
        <img src={RedPandaLogo} height={32} /> 

        <Typography variant="h6" noWrap component="div" sx={{ marginLeft: 2, marginRight: 1 }}>
          RP Tracker
        </Typography>

        <IconButton onClick={handleDrawerClose}>
          {theme.direction === 'rtl' ? <ChevronRightIcon /> : <ChevronLeftIcon />}
        </IconButton>
      </DrawerHeader>

      <Divider />

      <DrawerItems>
        {menuItems.map((item) => (
          <ListItem key={item.key} disablePadding sx={{ display: 'block' }}>
            <ListItemButton
              color='primary'
              onClick={() => handleMenuItemSelection(item)}
              sx={[
                {
                  minHeight: 48,
                  px: 2.5,
                  justifyContent: open ? 'initial' : "center"
                },
              ]}
            >
              <ListItemIcon
                color='primary'
                sx={[
                  {
                    minWidth: 0,
                    justifyContent: 'center',
                    mr: open ? 3 : "auto"
                  }
                ]}
              >
                {item.icon}
              </ListItemIcon>
              <ListItemText
                color='primary'
                primary={item.text}
                sx={[
                  {
                    opacity: open ? 1 : 0
                  }
                ]}
              />
            </ListItemButton>
          </ListItem>
        ))}
      </DrawerItems>
    </Drawer>
  );
}
