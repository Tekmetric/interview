import React from "react";
import { Outlet } from "react-router-dom";
import * as Layout from "../components/Layout";
import { Header } from "../components/Header";

export function Home() {
  return (
    <Layout.Root
      sx={{
        width: "100vw",
        height: "100vh",
        overflow: "hidden",
      }}
    >
      <Layout.Header>
        <Header />
      </Layout.Header>
      <Layout.Main>
        <Outlet />
      </Layout.Main>
    </Layout.Root>
  );
}
