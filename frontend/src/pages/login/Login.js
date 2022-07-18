import React, { useState } from 'react';

import Avatar from '@mui/material/Avatar';
import Button from '@mui/material/Button';
import Paper from '@mui/material/Paper';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import Container from '@mui/material/Container';

import { useForm } from 'react-hook-form';
import { useNavigate } from 'react-router-dom';

import loginService from './Login.service';

import './Login.scss';

function Login() {
  const navigate = useNavigate();

  const [loginError, setLoginError] = useState();
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    defaultValues: {
      username: 'kminchelle',
      password: '0lelplR',
    },
  });

  const onSubmit = async (data) => {
    const loginData = await loginService.login(data);

    if (loginData.error) {
      setLoginError(loginData.error.message);
    }

    navigate('/categories');
  };

  return (
    <Container component="main" maxWidth="xs" id="login">
      {/* <CssBaseline /> */}
      <Paper variant="outlined" square className="paper">
        <Avatar className="avatar">{/* <LockOutlinedIcon /> */}</Avatar>
        <Typography component="h1" variant="h5">
          Sign in
        </Typography>
        <form className="form" onSubmit={handleSubmit(onSubmit)} noValidate>
          <TextField
            variant="outlined"
            margin="normal"
            fullWidth
            id="username"
            label="Username"
            name="username"
            autoComplete="username"
            autoFocus
            // onChange={(e) => handleChange(e)}
            {...register('username', { required: true })}
            {...errors.email && { error: true, helperText: 'This field is required.' }}
          />

          <TextField
            variant="outlined"
            margin="normal"
            fullWidth
            name="password"
            label="Password"
            type="password"
            id="password"
            autoComplete="current-password"
            // onChange={(e) => handleChange(e)}
            {...register('password', { required: true })}
            {...errors.password && { error: true, helperText: 'This field is required.' }}
          />
          {loginError
            && (
            <Typography component="h6" color="red">
              <div>{ loginError }</div>
            </Typography>
            )}
          <Button type="submit" fullWidth variant="contained" color="primary" className="submit">
            Sign In
          </Button>
        </form>
      </Paper>
    </Container>
  );
}

export default Login;
