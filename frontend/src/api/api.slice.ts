import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';

import type { GetAllAnimesResponse } from '../types';
import { BASE_URL, API_PATHS, ANIME_PATHS } from './api.routes';

export const apiSlice = createApi({
  reducerPath: 'api',
  baseQuery: fetchBaseQuery({
    baseUrl: BASE_URL,
  }),
  endpoints: (builder) => ({
    // Anime paths
    getAllAnimes: builder.query<GetAllAnimesResponse[], void>({
      query: () => `${API_PATHS.ANIME}${ANIME_PATHS.GET_ALL_ANIMES}`,
    }),
  }),
});

export const { useGetAllAnimesQuery } = apiSlice;
