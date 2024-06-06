import { useContext } from "react";
import { authenticationContext } from "../contexts/authenticationContext";
import invariant from "invariant";

export function useAuthenticationContext() {
  const context = useContext(authenticationContext);

  invariant(
    context,
    "useAuthenticationContext must be used within a AuthenticationProvider"
  );

  return context;
}
