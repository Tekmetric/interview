import React from "react";
import { useAuth } from "../utils/hooks/auth";
import { CircularProgress } from "@mui/material";

interface AuthRequiredProps {
  children: React.ReactNode;
}

export function AuthRequired({ children }: AuthRequiredProps) {
  const { isAuthenticationLoaded } = useAuth();

  if (!isAuthenticationLoaded) {
    return <CircularProgress />;
  }

  return <>{children}</>;
}
