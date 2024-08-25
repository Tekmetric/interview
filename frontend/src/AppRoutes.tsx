import React from "react";
import { Route, Routes } from "react-router-dom";
import ListPage from "./pages/ListPage";
import CreatePage from "./pages/CreatePage";
import DetailsPage from "./pages/DetailsPage";
import NavBar from "./components/NavBar";
import LoginPage from "./pages/LoginPage";
import { AuthRequired } from "./pages/AuthRequired";

function AppRoutes() {
  return (
    <>
      <NavBar />
      <Routes>
        <Route path="/login" element={<LoginPage />} />

        <Route
          path="/"
          element={
            <AuthRequired>
              <ListPage />
            </AuthRequired>
          }
        />
        <Route
          path="/create"
          element={
            <AuthRequired>
              <CreatePage />
            </AuthRequired>
          }
        />
        <Route
          path="/details/:id"
          element={
            <AuthRequired>
              <DetailsPage />
            </AuthRequired>
          }
        />
      </Routes>
    </>
  );
}

export default AppRoutes;
