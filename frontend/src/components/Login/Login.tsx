import * as React from 'react';
import { AuthProvider, AppProvider, SignInPage } from '@toolpad/core';
import { defaultTheme } from '../../constants/theme.constants';
import { getTheme } from '../../themes/theme.helper';

const providers: AuthProvider[] = [
  { id: 'github', name: 'GitHub' },
  { id: 'google', name: 'Google' },
  { id: 'facebook', name: 'Facebook' },
];

const signIn: (provider: AuthProvider) => void = async (provider) => {
  const promise = new Promise<void>((resolve) => {
    setTimeout(() => {
      console.log(`Sign in with ${provider.id}`);
      resolve();
    }, 500);
  });
  return promise;
};

function OAuthSignInPage() {
  return (
    <AppProvider theme={getTheme(defaultTheme)}>
      <SignInPage signIn={signIn} providers={providers} />
    </AppProvider>
  );
}

export default function Login() {
  return <OAuthSignInPage />
}
