import React from "react";
import { PropsWithChildren } from "react";
import { Navigate } from "react-router-dom";
import { Routes } from "../constants/routes.constants";
import { useAppSelector } from "../store/hooks/hooks";

const AuthenticatedRoute: React.FC<PropsWithChildren> = ( { children } ) => {
  const session = useAppSelector(state => state.session);

  return (
    <>
      {session.isAuthenticated ? children : <Navigate to={Routes.login} />}
    </>
  );
}

export default AuthenticatedRoute;
