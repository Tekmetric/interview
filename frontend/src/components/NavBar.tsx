import React from "react";
import { AppBar, Toolbar, Typography, Button } from "@mui/material";
import { Link } from "react-router-dom";

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
      </Toolbar>
    </AppBar>
  );
}

export default NavBar;
