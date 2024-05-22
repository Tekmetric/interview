import React from "react";
import { Outlet } from "react-router-dom";
import Link from "../../components/Link/Link";

const Root = () => (
  <div className="flex place-content-center">
    <div className="grow max-w-6xl grid gap-4 grid-cols-1 pt-6 px-4">
      <header className="flex items-center">
        <h1 className="text-3xl font-medium text-slate-700 grow">Welcome to the GitHub Repo Listing app!</h1>
        <Link to="/about">About</Link>
      </header>
      <h4 className="text-md font-medium text-slate-700 py-2">
        This application is for listing GitHub repositories of users or
        organizations!
      </h4>
      <Outlet />
    </div>
  </div>
);

export default Root;
