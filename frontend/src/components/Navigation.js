import React from "react";
import { Link as RouterLink, useLocation } from "react-router-dom";
import {
  AppBar,
  Toolbar,
  Typography,
  Button,
  IconButton,
  Box,
} from "@mui/material";
import {
  Brightness4 as DarkModeIcon,
  Brightness7 as LightModeIcon,
  Home as HomeIcon,
  Info as InfoIcon,
  Api as ApiIcon,
} from "@mui/icons-material";
import { useTheme } from "../contexts/ThemeContext";

const Navigation = () => {
  const { isDarkMode, toggleTheme } = useTheme();
  const location = useLocation();

  const isActive = (path) => location.pathname === path;

  return (
    <AppBar position="static">
      <Toolbar>
        <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
          Interview App
        </Typography>

        <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
          <Button
            component={RouterLink}
            to="/"
            color="inherit"
            startIcon={<HomeIcon />}
            variant={isActive("/") ? "outlined" : "text"}
            sx={{
              borderColor: isActive("/") ? "white" : "transparent",
              "&:hover": {
                backgroundColor: "rgba(255, 255, 255, 0.1)",
              },
            }}
          >
            Home
          </Button>

          <Button
            component={RouterLink}
            to="/about"
            color="inherit"
            startIcon={<InfoIcon />}
            variant={isActive("/about") ? "outlined" : "text"}
            sx={{
              borderColor: isActive("/about") ? "white" : "transparent",
              "&:hover": {
                backgroundColor: "rgba(255, 255, 255, 0.1)",
              },
            }}
          >
            About
          </Button>

          <Button
            component={RouterLink}
            to="/api-data"
            color="inherit"
            startIcon={<ApiIcon />}
            variant={isActive("/api-data") ? "outlined" : "text"}
            sx={{
              borderColor: isActive("/api-data") ? "white" : "transparent",
              "&:hover": {
                backgroundColor: "rgba(255, 255, 255, 0.1)",
              },
            }}
          >
            API Data
          </Button>

          <IconButton
            onClick={toggleTheme}
            color="inherit"
            aria-label="toggle theme"
            sx={{
              ml: 2,
              "&:hover": {
                backgroundColor: "rgba(255, 255, 255, 0.1)",
              },
            }}
          >
            {isDarkMode ? <LightModeIcon /> : <DarkModeIcon />}
          </IconButton>
        </Box>
      </Toolbar>
    </AppBar>
  );
};

export default Navigation;
