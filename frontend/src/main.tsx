import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "./index.css";
import { createBrowserRouter, RouterProvider } from "react-router";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import LoginPage from "./pages/auth/login.tsx";
import RegisterPage from "./pages/auth/register.tsx";
import CoffeePage from "./pages/coffees/[:id].tsx";
import CoffeeListPage from "./pages/coffees";
import NewCoffeePage from "./pages/coffees/new.tsx";
import EditCoffeePage from "./pages/coffees/edit.tsx";
import AppLayout from "./layout/app.tsx";
import { ThemeProvider } from "./contexts/ThemeContext.tsx";
import { AuthProvider } from "./contexts/AuthContext.tsx";

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 5 * 60 * 1000, // 5 minutes
      gcTime: 10 * 60 * 1000, // 10 minutes
      retry: 1,
      refetchOnWindowFocus: false,
    },
  },
});

const router = createBrowserRouter([
  {
    Component: AppLayout,
    children: [
      { index: true, Component: CoffeeListPage },
      { path: "coffee/new", Component: NewCoffeePage },
      { path: "coffee/edit/:id", Component: EditCoffeePage },
      { path: "coffee/:id", Component: CoffeePage },
      {
        path: "auth",
        children: [
          { path: "login", Component: LoginPage },
          { path: "register", Component: RegisterPage },
        ],
      },
    ],
  },
]);

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <QueryClientProvider client={queryClient}>
      <ThemeProvider>
        <AuthProvider>
          <RouterProvider router={router} />
        </AuthProvider>
      </ThemeProvider>
    </QueryClientProvider>
  </StrictMode>,
);
