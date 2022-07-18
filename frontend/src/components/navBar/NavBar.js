import React from 'react';

// import loginActions from '../login/Login.actions';
// import { history } from '../utils/_History';

import AppBar from '@mui/material/AppBar';
import Button from '@mui/material/Button';
// import CssBaseline from '@material-ui/core/CssBaseline';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import { useNavigate } from 'react-router-dom';
import loginService from '../../pages/login/Login.service';

import './NavBar.scss';

// import { makeStyles } from '@material-ui/core/styles';

// const useStyles = makeStyles((theme) => ({
//   title: {
//     flexGrow: 1,
//   }
// }));

function NavBar() {
  // const classes = useStyles();
  const navigate = useNavigate();

  function handleLogout(e) {
    e.preventDefault();
    loginService.logout();
    navigate('/');
  }

  return (
    <>
      {/* <CssBaseline /> */}
      <AppBar position="fixed" id="navbar">
        <Toolbar>
          <Typography variant="h6" className="title">
            Products
          </Typography>
          <Button color="inherit" onClick={(e) => handleLogout(e)}>Logout</Button>
        </Toolbar>
      </AppBar>
    </>
  );
}

export default NavBar;
