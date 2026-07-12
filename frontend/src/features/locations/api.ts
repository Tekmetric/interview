import { rickAndMortyApi } from '../../api/rickAndMortyApi';
import { getNextPageNumber } from '../../api/pagination';
import type { Location, Paginated } from '../../api/types';
import { normalizeToArray } from '../../utils/normalizeToArray';

export interface LocationFilters {
  name?: string;
}

export const locationsApi = rickAndMortyApi.injectEndpoints({
  endpoints: (build) => ({
    getLocations: build.infiniteQuery<Paginated<Location>, LocationFilters, number>({
      infiniteQueryOptions: {
        initialPageParam: 1,
        getNextPageParam: (lastPage, _allPages, lastPageParam) =>
          getNextPageNumber(lastPage, lastPageParam),
      },
      query: ({ queryArg, pageParam }) => {
        const params = new URLSearchParams();
        if (queryArg.name) params.set('name', queryArg.name);
        params.set('page', String(pageParam));
        return `/location?${params.toString()}`;
      },
    }),

    getLocation: build.query<Location, number>({
      query: (id) => `/location/${id}`,
    }),

    getLocationsByIds: build.query<Location[], number[]>({
      query: (ids) => `/location/${ids.join(',')}`,
      transformResponse: (response: Location | Location[]) => normalizeToArray(response),
    }),
  }),
});

export const { useGetLocationsInfiniteQuery, useGetLocationQuery, useGetLocationsByIdsQuery } =
  locationsApi;
