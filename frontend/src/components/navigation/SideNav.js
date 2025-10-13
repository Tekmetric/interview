import { Link as RouterLink, useLocation } from "react-router-dom";
import {
  Drawer,
  List,
  ListItem,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Box,
  Divider,
  useTheme,
  BottomNavigation,
  BottomNavigationAction,
  Paper,
} from "@mui/material";
import {
  Home as HomeIcon,
  Info as InfoIcon,
  Pets as BirdIcon,
  Category as SpeciesIcon,
} from "@mui/icons-material";
import Text from "../../assets/Text";

const SideNav = () => {
  const location = useLocation();
  const theme = useTheme();

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
              onClick={undefined}
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

      {/* Mobile Bottom Navigation */}
      <Paper
        sx={{
          position: "fixed",
          bottom: 0,
          left: 0,
          right: 0,
          zIndex: theme.zIndex.appBar,
          display: { xs: "block", md: "none" },
          borderRadius: 0,
          boxShadow: theme.shadows[8],
        }}
        elevation={8}
      >
        <BottomNavigation
          value={location.pathname}
          sx={{
            height: 64,
            backgroundColor: theme.palette.background.paper,
            "& .MuiBottomNavigationAction-root": {
              minWidth: "auto",
              padding: "6px 8px",
              "&.Mui-selected": {
                color: theme.palette.primary.main,
              },
              "& .MuiBottomNavigationAction-label": {
                fontSize: "0.75rem",
                fontWeight: 500,
                "&.Mui-selected": {
                  fontSize: "0.75rem",
                  fontWeight: 600,
                },
              },
            },
          }}
        >
          {navigationItems.map((item) => (
            <BottomNavigationAction
              key={item.path}
              label={item.text}
              value={item.path}
              icon={item.icon}
              component={RouterLink}
              to={item.path}
            />
          ))}
        </BottomNavigation>
      </Paper>
    </>
  );
};

export default SideNav;
