import { createBrowserRouter, RouterProvider } from "react-router-dom";
import AppLayout from "../components/AppLayout/AppLayout";
import Fallback404Page from "./404/404Page";
import LoginPage from "./login/LoginPage";
import { Routes } from "../constants/routes.constants";
import HomePage from "./home/HomePage";
import LocationsPage from "./locations/LocationsPage";
import SightingsPage from "./sightings/SightingsPage";
import SightingsList from "./sightings/SightingsList";
import AddSighting from "./sightings/AddSighting";
import EditSighting from "./sightings/EditSighting";
import RedPandaPage from "./panda/RedPandaPage";
import RedPandaList from "./panda/RedPandaList";
import AddPanda from "./panda/AddPanda";
import EditPanda from "./panda/EditPanda";
import PandaDetail from "./panda/PandaDetail";

const AppRoutes: React.FC = () => {
  const router = createBrowserRouter([
    {
      path: Routes.login,
      element: <LoginPage />,
    },
    {
      path: Routes.home,
      element: <AppLayout />,
      children: [
        {
          path: Routes.home,
          element: <HomePage />,
        },       
        {
          path: Routes.locations,
          element: <LocationsPage />,
        },
        {
          path: Routes.sightings,
          element: <SightingsPage />,
          children: [
            {
              path: Routes.sightings,
              element: <SightingsList />,
            },
            {
              path: Routes.addSighting,
              element: <AddSighting />,
            },
            {
              path: Routes.editSighting,
              element: <EditSighting />,
            },
          ]
        },
        {
          path: Routes.pandas,
          element: <RedPandaPage />,
          children: [
            {
              path: Routes.pandas,
              element: <RedPandaList />,
            },
            {
              path: Routes.pandaDetail,
              element: <PandaDetail />,
            },
            {
              path: Routes.addPanda,
              element: <AddPanda />,
            },
            {
              path: Routes.editPanda,
              element: <EditPanda />,
            },
          ]
        },
      ]
    },
    {
      path: "*",
      element: <Fallback404Page />
    }
  ]);

  return <RouterProvider router={router} />;
}

export default AppRoutes;
