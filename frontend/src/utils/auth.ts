import { useAuth0 } from '@auth0/auth0-react';

export const useAuthFetch = () => {
  const { getAccessTokenSilently, isAuthenticated } = useAuth0();

  const authFetch = async (url: string, options: RequestInit = {}): Promise<Response> => {
    const headers: HeadersInit = {
      'Content-Type': 'application/json',
      ...((options.headers as Record<string, string>) || {}),
    };

    if (isAuthenticated) {
      try {
        const token = await getAccessTokenSilently();
        headers['Authorization'] = `Bearer ${token}`;
      } catch (error) {
        console.error('Error getting access token:', error);
        throw error;
      }
    }

    return fetch(url, {
      ...options,
      headers,
    });
  };

  return { authFetch, isAuthenticated };
};
