import React from 'react';
import { AuthProvider, AppProvider, SignInPage } from '@toolpad/core';
import { getTheme } from '../../themes/theme.helper';
import { Themes } from '../../types/Theme';
import { Box } from '@mui/material';
import { useAppDispatch, useAppSelector } from '../../store/hooks/hooks';
import { Navigate } from 'react-router-dom';
import { Routes } from '../../constants/routes.constants';
import { completeLogin } from '../../store/session.store';
import { UserService } from '../../service/UserService';

const providers: AuthProvider[] = [{ id: 'credentials', name: 'Email and Password' }];

const CredentialsSignInPage: React.FC = () => {
  const session = useAppSelector(state => state.session);
  const dispatch = useAppDispatch();

  const onLoginSuccess = () => {
    const username = "catrinel.c";
    dispatch(completeLogin(username));
  }

  const LoginForm = (
    <Box sx={{ display: 'flex', justifyContent: "space-evenly", alignItems: "center", width: '100vw'}}>
      <AppProvider theme={getTheme(Themes.Dark)}>
        <SignInPage
          signIn={(provider: AuthProvider, formData: FormData) => UserService.signIn(provider, formData, onLoginSuccess)}
          providers={providers}
        />
      </AppProvider>
    </Box>
  );

  return (
    <>
      {session.isAuthenticated ? <Navigate to={Routes.home} /> : LoginForm}
    </>
  );
}

export default CredentialsSignInPage;
