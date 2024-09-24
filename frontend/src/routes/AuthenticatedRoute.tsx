import React from "react";
import { PropsWithChildren, useLayoutEffect } from "react";
import { useNavigate } from "react-router-dom";
import { Routes } from "../constants/routes.constants";

export default function AuthenticatedRoute (props: PropsWithChildren) {
  const isAuthenticated = true; // TODO: wire in when login is functional const session = useAppSelector(state => state.session);

  const navigate = useNavigate();

  useLayoutEffect(() => {
    !isAuthenticated && navigate(Routes.login);
    //   if (session.isAuthenticated) {
    //     navigate(Routes.login);
    //   }
  }, [isAuthenticated]);  // }, [session.isAuthenticated]);

  return (
    <>
      {props.children}
    </>
  )

}
