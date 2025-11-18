import { configureStore } from '@reduxjs/toolkit';
import { eBirdApi } from './api/eBirdApi';

export const store = configureStore({
  reducer: {
    [eBirdApi.reducerPath]: eBirdApi.reducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat(eBirdApi.middleware),
});
