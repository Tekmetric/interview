import { createBrowserRouter, RouterProvider } from "react-router-dom";
import AppLayout from "../AppLayout/AppLayout";

const AppRoutes: React.FC = () => {
  const router = createBrowserRouter([
    {
      path: "/login",
      element: <div>Login page</div>,
    },
    {
      path: "/",
      element: <AppLayout />,
      children: [
        {
          path: "/",
          element: <div>Home</div>,
        },
        {
          path: "/add",
          element: <div>Add</div>,
        },
        {
          path: "/edit/:id",
          element: <div>Edit</div>,
        },
        {
          path: "/locations",
          element: <div>Locations</div>,
        },
        {
          path: "/list",
          element: <div>List</div>,
        },
      ]
    },
    {
      path: "*",
      element: <div>No match</div>
    }
  ]);

  return <RouterProvider router={router} />;
}

export default AppRoutes;
