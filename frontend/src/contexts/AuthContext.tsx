import { useEffect, useState } from "react";
import type { ReactNode } from "react";
import { useCurrentUser, type User } from "../hooks/useAuthQueries";
import { AuthContext } from "./authContext";

interface AuthProviderProps {
  children: ReactNode;
}

export function AuthProvider({ children }: AuthProviderProps) {
  const [user, setUser] = useState<User | null>(null);
  const [isInitialized, setIsInitialized] = useState(false);
  const { data: currentUser, isLoading, error } = useCurrentUser();

  // Initialize user from localStorage on first load
  useEffect(() => {
    const storedUser = localStorage.getItem("user");
    const token = localStorage.getItem("authToken");
    
    if (storedUser && token) {
      try {
        const userData = JSON.parse(storedUser);
        if (userData && userData.id) {
          setUser(userData);
        } else {
          // Invalid user data, clear storage
          localStorage.removeItem("authToken");
          localStorage.removeItem("user");
        }
      } catch {
        // Failed to parse, clear storage
        localStorage.removeItem("authToken");
        localStorage.removeItem("user");
      }
    }
    setIsInitialized(true);
  }, []);

  // Handle API response for current user
  useEffect(() => {
    if (isInitialized) {
      if (currentUser) {
        setUser(currentUser);
        // Update localStorage with fresh data
        localStorage.setItem("user", JSON.stringify(currentUser));
      } else if (error) {
        // API failed, clear everything
        localStorage.removeItem("authToken");
        localStorage.removeItem("user");
        setUser(null);
      }
    }
  }, [currentUser, error, isInitialized]);

  const logout = () => {
    localStorage.removeItem("authToken");
    localStorage.removeItem("user");
    setUser(null);
  };

  const value = {
    user,
    isAuthenticated: !!user,
    isLoading: isLoading || !isInitialized,
    logout,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
