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
        initialPageParam: 1,
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
      query: ({ pageParam }) => `${API_PATHS.ANIME}${ANIME_PATHS.GET_ALL_ANIMES}?page=${pageParam}`,
    }),
  }),
});

export const { useGetAllAnimesInfiniteQuery } = apiSlice;
