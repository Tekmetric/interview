import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";

// counterintuiviely, the token is stored in localStorage
export const SESSION_TOKEN_NAME = "session-token";

export const useAuth = () => {
  const [isAuthenticationLoaded, setIsAuthenticationLoaded] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem(SESSION_TOKEN_NAME);

    if (token) {
      setIsAuthenticationLoaded(true);
    } else {
      navigate("/login");
    }
  }, [navigate]);

  const setToken = (token: string) => {
    localStorage.setItem(SESSION_TOKEN_NAME, token);
  };

  const resetAuth = () => {
    localStorage.removeItem(SESSION_TOKEN_NAME);
  };

  return { isAuthenticationLoaded, setToken, resetAuth };
};
