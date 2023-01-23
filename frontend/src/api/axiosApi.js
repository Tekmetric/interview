import axios from 'axios';

const BASE_API_URL = process.env.REACT_APP_REST_API_URL;

export const axiosPublic = axios.create({
  baseURL: BASE_API_URL,
  timeout: 10000,
});

export const axiosPrivate = axios.create({
  baseURL: BASE_API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true,
});
