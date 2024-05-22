import React from "react";
import {
  createBrowserRouter,
  createRoutesFromElements,
  RouterProvider,
  Route,
} from "react-router-dom";

import Root from "./pages/Root/Root";
import RepositoryListing from "./pages/RepositoryListing/RepositoryListing";
import About from "./pages/About/About";
import PageNotFound from "./pages/PageNotFound/PageNotFound";

const router = createBrowserRouter(
  createRoutesFromElements(
    <Route path="/" element={<Root />}>
      <Route index element={<RepositoryListing />} />
      <Route path="about" element={<About />} />
      <Route path="*" element={<PageNotFound />} />
    </Route>
  )
);

const App = () => <RouterProvider router={router} />;

export default App;
