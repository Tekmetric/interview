import { createContext } from "react";
import { User } from "../types/User";

interface AuthenticationContextType {
  user: User | null;
}

export const authenticationContext = createContext<
  AuthenticationContextType | undefined
>(undefined);

export const AuthenticationProvider = authenticationContext.Provider;
