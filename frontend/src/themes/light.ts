import { createTheme } from "@mui/material";

export const lightTheme = createTheme({
  typography: {
    fontFamily: '"Montserrat", sans-serif'
  },
  palette: {
    mode: 'light',
    primary: {
      main: '#00796b',
    },
    secondary: {
      main: '#ff7d2e',
    },
    error: {
      main: '#c62828',
    },
    background: {
      default: '#fffff2',
      paper: '#fffff2',
    },
  },
});
