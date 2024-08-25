import { useQuery } from "@tanstack/react-query";
import { send } from "../send";
import { AuthResponse } from "../../typings/auth";

export const useAuth = () => {
  return useQuery<AuthResponse, Error>({
    queryKey: ["auth-status"],
    queryFn: async () => {
      const response = await send<AuthResponse>("GET", "/api/is-authenticated");
      return response;
    },
    staleTime: Infinity,
    refetchOnWindowFocus: false,
    refetchOnMount: false,
    refetchOnReconnect: false,
    retry: false,
  });
};
