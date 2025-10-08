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
    getAllAnimes: builder.infiniteQuery<GetAllAnimesResponse, void, number>({
      infiniteQueryOptions: {
        // Must provide a default initial page param value
        initialPageParam: 0,
        getNextPageParam: (
          lastPage,
          allPages,
          lastPageParam,
        ) => lastPageParam + 1,
        getPreviousPageParam: (
          firstPage,
          allPages,
          firstPageParam,
        ) => (firstPageParam > 0 ? firstPageParam - 1 : undefined),
      },
      query: () => `${API_PATHS.ANIME}${ANIME_PATHS.GET_ALL_ANIMES}`,
    }),
  }),
});

export const { useGetAllAnimesInfiniteQuery } = apiSlice;
