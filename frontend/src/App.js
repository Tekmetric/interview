import React from 'react';
import {
  BrowserRouter, Routes, Route, Navigate,
} from 'react-router-dom';

import PrivateRoute from './utils/PrivateRoute';
import Login from './pages/login/Login';
import Categories from './pages/categories/Categories';
import Products from './pages/products/Products';
import Product from './pages/product/Product';

export default function App() {
  return (
    <div className="App">
      <BrowserRouter>
        <Routes>
          {/* <PrivateRoute exact path="/" component={Properties} /> */}
          {/* <PrivateRoute exact path="/details/:id" component={PropertyDetails} /> */}
          <Route path="/" element={<Login />} />
          <Route path="*" element={<Navigate to="/" replace />} />
          <Route
            path="/categories"
            element={(
              <PrivateRoute>
                <Categories />
              </PrivateRoute>
            )}
          />
          <Route
            path="/categories/:category"
            element={(
              <PrivateRoute>
                <Products />
              </PrivateRoute>
            )}
          />
          <Route
            path="/categories/:category/:id"
            element={(
              <PrivateRoute>
                <Product />
              </PrivateRoute>
            )}
          />
        </Routes>
      </BrowserRouter>
    </div>
  );
}
