import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import { Container } from "@mui/material";
import { Provider } from "react-redux";
import { store } from "./store/store";
import { ThemeContextProvider } from "./contexts/ThemeContext";
import Navigation from "./components/Navigation";
import Home from "./components/Home";
import About from "./components/About";
import BirdData from "./components/BirdData";

const App = () => {
  return (
    <Provider store={store}>
      <ThemeContextProvider>
        <Router>
          <Navigation />
          <Container maxWidth="lg" sx={{ mt: 2, mb: 4 }}>
            <Routes>
              <Route path="/" element={<Home />} />
              <Route path="/about" element={<About />} />
              <Route path="/bird-data" element={<BirdData />} />
              <Route
                path="*"
                element={
                  <div>
                    <h1>404 - Page Not Found</h1>
                  </div>
                }
              />
            </Routes>
          </Container>
        </Router>
      </ThemeContextProvider>
    </Provider>
  );
};

export default App;
