import React from "react";
import { Link as RouterLink, useLocation } from "react-router-dom";
import { AppBar, Toolbar, Typography, IconButton, Box } from "@mui/material";
import {
  Brightness4 as DarkModeIcon,
  Brightness7 as LightModeIcon,
} from "@mui/icons-material";
import { useTheme } from "../../contexts/ThemeContext";
import Text from "../../assets/Text";

const Header = () => {
  const { isDarkMode, toggleTheme } = useTheme();
  const drawerWidth = 280;

  return (
    <AppBar
      position="fixed"
      sx={{
        zIndex: (theme) => theme.zIndex.drawer + 1,
        ml: { md: `${drawerWidth}px` },
        width: { md: `calc(100% - ${drawerWidth}px)` },
      }}
    >
      <Toolbar>
        <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
          {Text.header.title}
        </Typography>

        <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
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

export default Header;
