import React from "react";
import { AppBar, Toolbar, Button } from "@mui/material";
import { Link, useNavigate } from "react-router-dom";
import { logout } from "../utils/api/auth";
import { useAuth } from "../contexts/AuthContext";
import styled from "@emotion/styled";

const AppBarStyled = styled(AppBar)`
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  background-color: #454140;
`;

function NavBar() {
  const navigate = useNavigate();
  const { isAuthenticated, resetAuth } = useAuth();

  if (isAuthenticated === false) {
    return null;
  }
  return (
    <AppBarStyled position="static">
      <Toolbar style={{ gap: "8px" }}>
        <Button component={Link} to="/" color="inherit">
          Events
        </Button>
        <Button component={Link} to="/create" color="inherit">
          Create Event
        </Button>
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
    </AppBarStyled>
  );
}

export default NavBar;
