import React from "react";
import { AppBar, Toolbar, Typography, Button } from "@mui/material";
import { Link, useNavigate } from "react-router-dom";
import { logout } from "../utils/api/auth";
import { useQueryClient } from "@tanstack/react-query";

// const navBarStyles = css`
//   flexgrow: 1;
// `;

// const titleStyles = css({
//   flexGrow: 1,
//   textDecoration: "none",
//   color: "inherit",
// });

// const buttonStyles = css({
//   textTransform: "none",
// });

function NavBar() {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  return (
    <AppBar position="static">
      <Toolbar>
        <Link to="/">
          <Typography variant="h6">Event Management</Typography>
        </Link>
        <Link to="/">
          <Button color="inherit">Events</Button>
        </Link>
        <Link to="/create">
          <Button color="inherit">Create Event</Button>
        </Link>
        <Button
          color="inherit"
          onClick={() => {
            logout().then(() => {
              queryClient.invalidateQueries({ queryKey: ["auth-status"] });
              navigate("/login");
            });
          }}
        >
          Logout
        </Button>
      </Toolbar>
    </AppBar>
  );
}

export default NavBar;
