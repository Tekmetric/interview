import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import { Container } from "@mui/material";
import { ThemeContextProvider } from "./contexts/ThemeContext";
import Navigation from "./components/Navigation";
import Home from "./components/Home";
import About from "./components/About";

const App = () => {
  return (
    <ThemeContextProvider>
      <Router>
        <Navigation />
        <Container maxWidth="lg" sx={{ mt: 2, mb: 4 }}>
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/about" element={<About />} />
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
  );
};

export default App;
