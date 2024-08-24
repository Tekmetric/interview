import React from "react";
import AppRoutes from "./AppRoutes";
import { BrowserRouter } from "react-router-dom";
//import logo from './logo.svg';

function App() {
  return (
    <BrowserRouter>
      <AppRoutes />
    </BrowserRouter>
  );
}

export default App;
