import { useState } from "react";
import { Link as RouterLink, useLocation } from "react-router-dom";
import {
  Drawer,
  List,
  ListItem,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  IconButton,
  Box,
  Divider,
  useTheme,
  useMediaQuery,
} from "@mui/material";
import {
  Menu as MenuIcon,
  Home as HomeIcon,
  Info as InfoIcon,
  Pets as BirdIcon,
  Category as SpeciesIcon,
  LocationOn as HotspotIcon,
  Public as RegionIcon,
  Timeline as ActivityIcon,
} from "@mui/icons-material";
import Text from "../../assets/Text";

const SideNav = () => {
  const [isOpen, setIsOpen] = useState(false);
  const location = useLocation();
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down("md"));

  const toggleDrawer = () => {
    setIsOpen(!isOpen);
  };

  const handleClose = () => {
    setIsOpen(false);
  };

  const isActive = (path) => location.pathname === path;

  const navigationItems = [
    {
      text: Text.navigation.home,
      path: "/",
      icon: <HomeIcon />,
    },
    {
      text: Text.navigation.about,
      path: "/about",
      icon: <InfoIcon />,
    },
    {
      text: Text.navigation.birdData,
      path: "/bird-data",
      icon: <BirdIcon />,
    },
    {
      text: Text.navigation.species,
      path: "/species",
      icon: <SpeciesIcon />,
    },
    {
      text: Text.navigation.hotspots,
      path: "/hotspots",
      icon: <HotspotIcon />,
    },
    {
      text: Text.navigation.regions,
      path: "/regions",
      icon: <RegionIcon />,
    },
    {
      text: Text.navigation.activity,
      path: "/activity",
      icon: <ActivityIcon />,
    },
  ];

  const drawerWidth = 280;

  const drawerContent = (
    <Box
      sx={{
        width: drawerWidth,
        height: "100%",
        backgroundColor: theme.palette.background.paper,
      }}
    >
      <Box
        sx={{
          p: 2,
          borderBottom: `1px solid ${theme.palette.divider}`,
        }}
      >
        <Box
          component="img"
          sx={{
            height: 40,
            width: "auto",
            display: "block",
            margin: "0 auto",
          }}
          alt="Logo"
          src="/logo.svg"
          onError={(e) => {
            e.target.style.display = "none";
          }}
        />
      </Box>

      <List sx={{ pt: 1 }}>
        {navigationItems.map((item) => (
          <ListItem key={item.path} disablePadding>
            <ListItemButton
              component={RouterLink}
              to={item.path}
              onClick={isMobile ? handleClose : undefined}
              selected={isActive(item.path)}
              sx={{
                mx: 1,
                mb: 0.5,
                borderRadius: 2,
                "&.Mui-selected": {
                  backgroundColor: theme.palette.primary.main,
                  color: theme.palette.primary.contrastText,
                  "&:hover": {
                    backgroundColor: theme.palette.primary.dark,
                  },
                  "& .MuiListItemIcon-root": {
                    color: theme.palette.primary.contrastText,
                  },
                },
                "&:hover": {
                  backgroundColor: theme.palette.action.hover,
                },
              }}
            >
              <ListItemIcon
                sx={{
                  color: isActive(item.path)
                    ? theme.palette.primary.contrastText
                    : theme.palette.text.secondary,
                  minWidth: 40,
                }}
              >
                {item.icon}
              </ListItemIcon>
              <ListItemText
                primary={item.text}
                sx={{
                  "& .MuiListItemText-primary": {
                    fontSize: "0.95rem",
                    fontWeight: isActive(item.path) ? 600 : 400,
                  },
                }}
              />
            </ListItemButton>
          </ListItem>
        ))}
      </List>

      <Divider sx={{ mt: 2 }} />

      <Box
        sx={{
          p: 2,
          mt: "auto",
          textAlign: "center",
          color: theme.palette.text.secondary,
          fontSize: "0.75rem",
        }}
      >
        eBird API Explorer
      </Box>
    </Box>
  );

  return (
    <>
      {/* Menu Button for Mobile */}
      <IconButton
        color="inherit"
        aria-label="open drawer"
        edge="start"
        onClick={toggleDrawer}
        sx={{
          position: "fixed",
          top: 16,
          left: 16,
          zIndex: theme.zIndex.appBar + 1,
          backgroundColor: theme.palette.primary.main,
          color: theme.palette.primary.contrastText,
          "&:hover": {
            backgroundColor: theme.palette.primary.dark,
          },
          display: { md: "none" },
        }}
      >
        <MenuIcon />
      </IconButton>

      {/* Desktop Drawer */}
      <Drawer
        variant="permanent"
        sx={{
          display: { xs: "none", md: "block" },
          "& .MuiDrawer-paper": {
            boxSizing: "border-box",
            width: drawerWidth,
            border: "none",
            boxShadow: theme.shadows[1],
          },
        }}
        open
      >
        {drawerContent}
      </Drawer>

      {/* Mobile Drawer */}
      <Drawer
        variant="temporary"
        open={isOpen}
        onClose={handleClose}
        ModalProps={{
          keepMounted: true, // Better open performance on mobile.
        }}
        sx={{
          display: { xs: "block", md: "none" },
          "& .MuiDrawer-paper": {
            boxSizing: "border-box",
            width: drawerWidth,
          },
        }}
      >
        {drawerContent}
      </Drawer>
    </>
  );
};

export default SideNav;
