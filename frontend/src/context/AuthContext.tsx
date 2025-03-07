import { ReactNode, useEffect, useState, createContext } from "react";
import User from "../types/user";
import { getActiveUser } from "../services/authService";


export interface AuthContextType {
  user: User | null;
  setUser: (user: User | null) => void;
}

export const AuthContext = createContext<AuthContextType | null>(null);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [user, setUser] = useState<User | null>(null);

  useEffect(() => {
    const activeUser = getActiveUser();
    if (activeUser) {
      setUser(activeUser);
    }
    else {
      setUser(null);
    }
  }, []);

  return (
    <AuthContext.Provider value={{ user, setUser }}>
      {children}
    </AuthContext.Provider>
  );
};
