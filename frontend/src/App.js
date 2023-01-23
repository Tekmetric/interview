import React, { Component } from "react";
import HomePage from "views/home/HomePage";
import ShopListPage from "views/shop/ShopListPage";
import NotFoundPage from "views/error/NotFoundPage";
import { StorageProvider } from "contexts/storageContext";
import { userPersistence } from "helpers/userPersistence";
import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import RoutesConfig from "routes";
import AuthenticationRequired from "components/auth/AuthenticationRequired";
import { AuthProvider } from "contexts/authContext";
import LoginPage from "views/login/LoginPage";
import { Toaster } from "react-hot-toast";

class App extends Component {
  render() {
    return (
      <div className="App">
        <StorageProvider storage={userPersistence}>
          <AuthProvider>
            <BrowserRouter>
              <Routes>
                <Route path={RoutesConfig.home} element={<HomePage />} />
                <Route path={RoutesConfig.login} element={<LoginPage />} />

                <Route element={<AuthenticationRequired />}>
                    <Route path={RoutesConfig.shopList} element={<ShopListPage />} />
                </Route>                
                <Route path={RoutesConfig.error404} element={<NotFoundPage />} />
                <Route
                  path="*"
                  element={<Navigate to={RoutesConfig.error404} />}
                />
              </Routes>
            </BrowserRouter>
          </AuthProvider>
        </StorageProvider>
        <Toaster position="top-right" />
      </div>
    );
  }
}

export default App;
