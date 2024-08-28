import React from "react";
import * as Layout from "../components/Layout";
import { Header } from "../components/Header";
import { Alert, Card } from "@mui/material";
import CheckIcon from "@mui/icons-material/Check";

export function NotFound404() {
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
        <Card sx={{ width: '50%', margin: '50px 0 0 25%'}}>
          <Alert icon={<CheckIcon fontSize="inherit" />} severity="success">
            Here is a gentle confirmation that your action was successful.
          </Alert>
        </Card>
      </Layout.Main>
    </Layout.Root>
  );
}
