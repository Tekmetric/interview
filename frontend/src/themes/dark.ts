import { createTheme } from "@mui/material";

export const darkTheme = createTheme({
  typography: {
    fontFamily: '"Montserrat", sans-serif'
  },
  palette: {
    mode: 'dark',
    primary: {
      main: '#03CBBB',
    },
    secondary: {
      main: '#5703CB',
    },
    error: {
      main: '#c62828',
    },
    background: {
      default: '#121212',
      paper: '#2d2d2d',
    },
  },
});
