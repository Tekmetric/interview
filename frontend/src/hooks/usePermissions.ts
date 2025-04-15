import { useAuth0 } from '@auth0/auth0-react';
// No React hooks needed here
import { jwtDecode } from 'jwt-decode';

const AUTH0_USER_READ_PERMISSION = 'read';
const AUTH0_USER_WRITE_PERMISSION = 'write';

interface JwtPayload {
  permissions?: string[];
  [key: string]: any;
}

export const usePermissions = () => {
  const { getAccessTokenSilently, isAuthenticated } = useAuth0();

  const hasPermission = async (permission: string): Promise<boolean> => {
    if (!isAuthenticated) return false;

    try {
      const token = await getAccessTokenSilently();

      const decodedToken = jwtDecode<JwtPayload>(token);

      const permissions = decodedToken.permissions || [];
      return permissions.includes(permission);
    } catch (error) {
      console.error('Error getting permissions from token:', error);
      return false;
    }
  };

  const hasWritePermission = (): Promise<boolean> => {
    return hasPermission(AUTH0_USER_WRITE_PERMISSION);
  };

  const hasReadPermission = (): Promise<boolean> => {
    return hasPermission(AUTH0_USER_READ_PERMISSION);
  };

  return {
    hasPermission,
    hasWritePermission,
    hasReadPermission,
    isAuthenticated,
  };
};
