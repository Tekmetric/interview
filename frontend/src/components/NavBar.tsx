import React from "react";
import { AppBar, Toolbar, Typography, Button } from "@mui/material";
import { Link, useNavigate } from "react-router-dom";
import { logout } from "../utils/api/auth";
import { useAuth } from "../utils/hooks/auth";

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
  const { resetAuth } = useAuth();
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
              resetAuth();
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
