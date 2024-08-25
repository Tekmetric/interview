import React from "react";
import { Navigate } from "react-router-dom";
import { useAuth } from "../utils/hooks/auth";
import { CircularProgress } from "@mui/material";

interface AuthRequiredProps {
  children: React.ReactNode;
}

export function AuthRequired({ children }: AuthRequiredProps) {
  const { data, isLoading, isError } = useAuth();

  if (isLoading) {
    return <CircularProgress />;
  }

  if (isError || !data?.is_authenticated) {
    return <Navigate to="/login" />;
  }

  return <>{children}</>;
}
