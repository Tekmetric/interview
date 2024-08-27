import React, { createContext, useContext, useState, useEffect } from "react";

export const SESSION_TOKEN_NAME = "session-token";

interface AuthContextType {
  isAuthenticated: boolean | null;
  setToken: (token: string) => void;
  resetAuth: () => void;
}

const defaultContext = {
  isAuthenticated: null,
  setToken: () => {},
  resetAuth: () => {},
};

export const AuthContext = createContext<AuthContextType>(defaultContext);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean | null>(null);

  useEffect(() => {
    const token = localStorage.getItem(SESSION_TOKEN_NAME);

    if (token) {
      setIsAuthenticated(true);
    } else {
      setIsAuthenticated(false);
    }
  }, []);

  const setToken = (token: string) => {
    localStorage.setItem(SESSION_TOKEN_NAME, token);
    setIsAuthenticated(true);
  };

  const resetAuth = () => {
    localStorage.removeItem(SESSION_TOKEN_NAME);
    setIsAuthenticated(false);
  };

  return (
    <AuthContext.Provider value={{ isAuthenticated, setToken, resetAuth }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
};
