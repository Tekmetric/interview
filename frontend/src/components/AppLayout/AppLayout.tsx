import * as React from 'react';
import Box from '@mui/material/Box';
import CssBaseline from '@mui/material/CssBaseline';
import { Outlet } from 'react-router-dom';
import { ThemeProvider } from '@mui/material';
import { Themes } from '../../types/Theme';
import SideBar from '../SideBar/SideBar';
import TopBar from '../TopBar/TopBar';
import { DrawerHeader } from '../SideBar/Sidebar.style';
import { getTheme } from '../../themes/theme.helper';
import { defaultTheme } from '../../constants/theme.constants';
import { SnackbarProvider } from 'notistack';

export default function AppLayout() {
  const [selectedTheme, setSelectedTheme] = React.useState<Themes>(defaultTheme);
  const [isSideBarExpanded, setIsSideBarExpanded] = React.useState(false);

  return (
    <ThemeProvider theme={getTheme(selectedTheme)}>
      <CssBaseline />
      <Box sx={{ display: 'flex', height: "calc(100% - 80px)" }}>
        <CssBaseline />

        <TopBar open={isSideBarExpanded} setOpen={setIsSideBarExpanded} theme={selectedTheme} setTheme={setSelectedTheme} />
        <SideBar open={isSideBarExpanded} setOpen={setIsSideBarExpanded} />

        <Box component="main" sx={{ flexGrow: 1 }}>
          <DrawerHeader />

          <Box sx={{ flexGrow: 1, p: 5, width: "100%", height: "100%" }}>
            <SnackbarProvider>
              <Outlet />
            </SnackbarProvider>
          </Box>
        </Box>
      </Box>
    </ThemeProvider>
  );
}
