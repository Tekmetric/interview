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
      main: '#5703CB',
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
