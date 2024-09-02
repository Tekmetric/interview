import React from "react";
import { Toolbar, Typography, Grid2 } from "@mui/material";
import { NavLink } from "react-router-dom";

export function Header() {
  return (
    <Grid2 container spacing={2} sx={{ flexGrow: 1 }}>
      <Toolbar sx={{ display: 'flex', alignItems: 'center', flexGrow: 1, justifyContent: 'space-between'}}>
        <img
          src="https://cdn.prod.website-files.com/641b3370aff0aec47a121975/646316aea37cf9d89278a5e2_tekmetric_wordmark.svg"
          loading="lazy"
          width="Auto"
          height="20"
          alt="Tekmetric logo."
        />

        <div
          style={{
            flexGrow: 1,
            display: "flex",
            justifyContent: "space-between",
            width: "100%",
            margin: "0 2em",
          }}
        >
          <NavLink to="/dashboard">
            <Typography variant="h6">Dashboard</Typography>
          </NavLink>
        </div>
      </Toolbar>
    </Grid2>
  );
}
