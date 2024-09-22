import * as React from 'react';
import { AuthProvider, AppProvider, SignInPage } from '@toolpad/core';
import { getTheme } from '../../themes/theme.helper';
import { Themes } from '../../types/Theme';
import { Box } from '@mui/material';

const providers: AuthProvider[] = [{ id: 'credentials', name: 'Email and Password' }];

const signIn: (provider: AuthProvider, formData: FormData) => void = async (
  provider,
  formData,
) => {
  const promise = new Promise<void>((resolve) => {
    setTimeout(() => {
      alert(
        `Signing in with "${provider.name}" and credentials: ${formData.get('email')}, ${formData.get('password')}`,
      );
      resolve();
    }, 300);
  });
  return promise;
};

export default function CredentialsSignInPage() {
  return (
    <Box sx={{ display: 'flex', justifyContent: "space-evenly", alignItems: "center", width: '100vw'}}>
      <AppProvider theme={getTheme(Themes.Dark)}>
        <SignInPage signIn={signIn} providers={providers} />
      </AppProvider>
    </Box>
  );
}
