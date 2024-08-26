import { useQuery } from "@tanstack/react-query";
import { AuthResponse } from "../../typings/auth";
import { isAuthenticated } from "../api/auth";

export const useAuth = () => {
  return useQuery<AuthResponse, Error>({
    queryKey: ["auth-status"],
    queryFn: async () => isAuthenticated(),
    staleTime: Infinity,
    refetchOnWindowFocus: false,
    refetchOnMount: false,
    refetchOnReconnect: false,
    retry: false,
  });
};
