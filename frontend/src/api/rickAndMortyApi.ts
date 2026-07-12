import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';

// Single API slice for the whole app: one cache, one middleware.
// Each feature injects its own endpoints (see features/*/api.ts), so the
// endpoint definitions live next to the components that use them.
export const rickAndMortyApi = createApi({
  reducerPath: 'rickAndMortyApi',
  baseQuery: fetchBaseQuery({ baseUrl: 'https://rickandmortyapi.com/api' }),
  endpoints: () => ({}),
});
