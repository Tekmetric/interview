import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import { Box, Container, useTheme } from "@mui/material";
import { Provider } from "react-redux";
import { store } from "./store/store";
import { ThemeContextProvider } from "./contexts/ThemeContext";
import Header from "./components/navigation/Header";
import SideNav from "./components/navigation/SideNav";
import NotFound from "./components/navigation/NotFound";
import Home from "./components/pages/Home";
import About from "./components/pages/About";
import BirdData from "./components/pages/BirdData";
import Species from "./components/pages/Species";
import SpeciesDetail from "./components/pages/SpeciesDetail";

// Layout component to handle responsive design
const AppLayout = () => {
  const theme = useTheme();
  const drawerWidth = 280;

  return (
    <Box sx={{ display: "flex", minHeight: "100vh" }}>
      {/* Header */}
      <Header />

      {/* Sidebar */}
      <SideNav />

      {/* Main Content */}
      <Box
        component="main"
        sx={{
          flexGrow: 1,
          ml: { md: `${drawerWidth}px` },
          mt: { xs: 8, md: 8 }, // Account for header height only
          mb: { xs: 8, md: 0 }, // Account for bottom navigation on mobile (64px = 8*8px)
          p: 3,
          backgroundColor: theme.palette.background.default,
          minHeight: "calc(100vh - 64px)",
        }}
      >
        <Container maxWidth="xl" sx={{ px: { xs: 1, sm: 2, md: 3 } }}>
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/about" element={<About />} />
            <Route path="/bird-data" element={<BirdData />} />
            <Route path="/species" element={<Species />} />
            <Route path="/species/:speciesCode" element={<SpeciesDetail />} />
            <Route path="*" element={<NotFound />} />
          </Routes>
        </Container>
      </Box>
    </Box>
  );
};

const App = () => {
  return (
    <Provider store={store}>
      <ThemeContextProvider>
        <Router>
          <AppLayout />
        </Router>
      </ThemeContextProvider>
    </Provider>
  );
};

export default App;
