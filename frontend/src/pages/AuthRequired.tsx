import React from "react";
import { useAuth } from "../contexts/AuthContext";
import { Navigate } from "react-router-dom";

interface AuthRequiredProps {
  children: React.ReactNode;
}

export function AuthRequired({ children }: AuthRequiredProps) {
  const { isAuthenticated } = useAuth();

  if (isAuthenticated === false) {
    return <Navigate to="/login" />;
  }

  return <>{children}</>;
}
