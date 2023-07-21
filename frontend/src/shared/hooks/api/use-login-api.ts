import { useMutation } from '@tanstack/react-query';
import { AuthDataType, LoginDataType } from '../../types/login';

export const useLoginApi = (authData: LoginDataType) => {
  const authDataQueryString = new URLSearchParams(authData).toString();

  return useMutation({
    mutationKey: ['mutation:login'],
    mutationFn: async () => {
      const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/auth/oauth/token`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: authDataQueryString,
      });

      if (!response.ok) {
        throw new Error('Login failed');
      }

      const result: AuthDataType = await response.json();
      console.log('Success:', result);
      return result;
    },
  });
};