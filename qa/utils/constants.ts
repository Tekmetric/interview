export const BASE_URL = 'https://automationintesting.online';
export const API_BASE_URL = 'https://restful-booker.herokuapp.com';

export const ENDPOINTS = {
  AUTH: {
    LOGIN: '/auth',
  },
  PLATFORM_AUTH: {
    LOGIN: '/api/auth/login',
  },
  BOOKING: {
    BASE: '/booking',
    BY_ID: (id: number) => `/booking/${id}`,
  },
} as const;

export const ADMIN_CREDENTIALS = {
  USERNAME: 'admin',
  PASSWORD: 'password',
};

export const RESTFUL_BOOKER_CREDENTIALS = {
  USERNAME: 'admin',
  PASSWORD: 'password123',
};

export const TIMEOUTS = {
  NAVIGATION: 30000,
  ACTION: 10000,
  API: 15000,
} as const;
