import React from "react";
import { RouterProvider } from 'react-router-dom';
import router from "./router"
import { QueryClient, QueryClientProvider } from "react-query";
import { CountriesContextProvider } from "./context/CountriesContext";

const queryClient = new QueryClient();

export default function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <CountriesContextProvider>
        <RouterProvider router={router} />
      </CountriesContextProvider>
    </QueryClientProvider>
  );
}