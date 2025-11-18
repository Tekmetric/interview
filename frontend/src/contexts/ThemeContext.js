import React, { createContext, useContext, useState } from "react";
import { createTheme, ThemeProvider } from "@mui/material/styles";
import { CssBaseline } from "@mui/material";

const ThemeContext = createContext();

export const useTheme = () => {
  const context = useContext(ThemeContext);
  if (!context) {
    throw new Error("useTheme must be used within a ThemeContextProvider");
  }
  return context;
};

export const ThemeContextProvider = ({ children }) => {
  const [isDarkMode, setIsDarkMode] = useState(true);

  const lightTheme = createTheme({
    palette: {
      mode: "light",
      primary: {
        main: "#42a5f5", // Light blue
      },
      secondary: {
        main: "#dc004e",
      },
      background: {
        default: "#f5f5f5",
        paper: "#ffffff",
      },
    },
    shape: {
      borderRadius: 12,
    },
    components: {
      MuiButton: {
        styleOverrides: {
          root: {
            borderRadius: 12,
            textTransform: "none",
          },
        },
      },
      MuiPaper: {
        styleOverrides: {
          root: {
            borderRadius: 12,
          },
        },
      },
      MuiCard: {
        styleOverrides: {
          root: {
            borderRadius: 12,
          },
        },
      },
      MuiTextField: {
        styleOverrides: {
          root: {
            "& .MuiOutlinedInput-root": {
              borderRadius: 12,
            },
          },
        },
      },
      MuiSelect: {
        styleOverrides: {
          root: {
            borderRadius: 12,
          },
        },
      },
      MuiChip: {
        styleOverrides: {
          root: {
            borderRadius: 12,
          },
        },
      },
      MuiAlert: {
        styleOverrides: {
          root: {
            borderRadius: 12,
          },
        },
      },
      MuiDrawer: {
        styleOverrides: {
          paper: {
            borderRadius: "0 12px 12px 0",
          },
        },
      },
      MuiAppBar: {
        styleOverrides: {
          root: {
            borderRadius: 0,
          },
        },
      },
    },
  });

  const darkTheme = createTheme({
    palette: {
      mode: "dark",
      primary: {
        main: "#ad1457", // Dark pink
      },
      secondary: {
        main: "#f48fb1",
      },
      background: {
        default: "#121212",
        paper: "#1e1e1e",
      },
      text: {
        primary: "#ffffff", // Pure white for primary text
        secondary: "#e0e0e0", // Light gray for secondary text
      },
    },
    shape: {
      borderRadius: 12,
    },
    components: {
      MuiButton: {
        styleOverrides: {
          root: {
            borderRadius: 12,
            textTransform: "none",
          },
        },
      },
      MuiPaper: {
        styleOverrides: {
          root: {
            borderRadius: 12,
          },
        },
      },
      MuiCard: {
        styleOverrides: {
          root: {
            borderRadius: 12,
          },
        },
      },
      MuiTextField: {
        styleOverrides: {
          root: {
            "& .MuiOutlinedInput-root": {
              borderRadius: 12,
            },
          },
        },
      },
      MuiSelect: {
        styleOverrides: {
          root: {
            borderRadius: 12,
          },
        },
      },
      MuiChip: {
        styleOverrides: {
          root: {
            borderRadius: 12,
          },
        },
      },
      MuiAlert: {
        styleOverrides: {
          root: {
            borderRadius: 12,
          },
        },
      },
      MuiDrawer: {
        styleOverrides: {
          paper: {
            borderRadius: "0 12px 12px 0",
          },
        },
      },
      MuiAppBar: {
        styleOverrides: {
          root: {
            borderRadius: 0,
          },
        },
      },
      MuiTypography: {
        styleOverrides: {
          root: {
            color: "#ffffff", // Ensure all typography is white
          },
          body2: {
            color: "#e0e0e0", // Slightly dimmed for body2 text
          },
        },
      },
      MuiTableCell: {
        styleOverrides: {
          root: {
            color: "#ffffff", // White text in table cells
          },
        },
      },
      MuiListItemText: {
        styleOverrides: {
          primary: {
            color: "#ffffff", // White text in list items
          },
          secondary: {
            color: "#e0e0e0", // Light gray for secondary list text
          },
        },
      },
    },
  });

  const currentTheme = isDarkMode ? darkTheme : lightTheme;

  const toggleTheme = () => {
    setIsDarkMode(!isDarkMode);
  };

  return (
    <ThemeContext.Provider value={{ isDarkMode, toggleTheme }}>
      <ThemeProvider theme={currentTheme}>
        <CssBaseline />
        {children}
      </ThemeProvider>
    </ThemeContext.Provider>
  );
};
