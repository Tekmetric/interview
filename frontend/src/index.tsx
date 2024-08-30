import React from "react";
import ReactDOM from "react-dom/client";
import "./index.css";
import App from "./App";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { AuthProvider } from "./contexts/AuthContext";
import posthog from "posthog-js";

const rootElement = document.getElementById("root")!;
const root = ReactDOM.createRoot(rootElement);

posthog.init(process.env.REACT_APP_POSTHOG_KEY!, {
  api_host: process.env.REACT_APP_POSTHOG_HOST,
  person_profiles: "identified_only",
});

const queryClient = new QueryClient();

root.render(
  <QueryClientProvider client={queryClient}>
    <AuthProvider>
      <App />
    </AuthProvider>
  </QueryClientProvider>
);
