import { axiosPublic, axiosPrivate } from 'api/axiosApi';
import { userPersistence } from 'helpers/userPersistence';

export const authService = {
  refreshToken: () => {
    return axiosPublic.get('/auth/access-token', { withCredentials: true });
  },

  register: (data) => {
    return axiosPublic.post(
      '/api/auth/signup',
      {
        ...data,
      },
      {
        headers: { 'Content-Type': 'application/json' },
        withCredentials: true,
      }
    );
  },

  login: (data) => {
    return axiosPublic.post(
      '/auth/login',
      {
        ...data,
      },
      {
        headers: { 'Content-Type': 'application/json' },
        withCredentials: true,
      }
    );
  },

  logout: (data) => {
    return axiosPrivate.post(
      '/logout',
      {
        ...data,
      },
      {
        headers: {
          'Content-Type': 'application/json',
          Authorization: 'Bearer ' + userPersistence.accessToken(),
        },
        withCredentials: true,
      }
    );
  },
};
