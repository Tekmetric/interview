import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';

import type { GetAllAnimesResponse, GetAnimeByIdResponse } from '../../types';
import { BASE_URL, API_PATHS, ANIME_PATHS } from './api.routes';

export const apiSlice = createApi({
  reducerPath: 'anime-api',
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
    // Note: unfortunately, this public API exposes the same structure for one anime as it is in the list of all the animes
    // I added this query (which fetches some data that the app already has) just to showcase the difference between
    // infiniteQuery and query
    getAnimeById: builder.query<GetAnimeByIdResponse, string>({
      query: (id) => `${API_PATHS.ANIME}/${id}`,
    }),
  }),
});

export const { useGetAllAnimesInfiniteQuery, useGetAnimeByIdQuery } = apiSlice;
