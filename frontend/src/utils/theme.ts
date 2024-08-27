import { createTheme } from "@mui/material/styles";

const theme = createTheme({
  palette: {
    primary: {
      main: "#bd5734",
    },
    error: {
      main: "#7a3b2e",
    },
    secondary: {
      main: "#a79e84",
    },
  },
  typography: {
    fontFamily: "'Montserrat', sans-serif",
  },
});

export default theme;
