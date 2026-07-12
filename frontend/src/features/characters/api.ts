import { rickAndMortyApi } from '../../api/rickAndMortyApi';
import { getNextPageNumber } from '../../api/pagination';
import type { Character, Paginated } from '../../api/types';
import { normalizeToArray } from '../../utils/normalizeToArray';

export interface CharacterFilters {
  name?: string;
  status?: string;
  gender?: string;
}

function toSearchParams(filters: CharacterFilters, page: number): string {
  const params = new URLSearchParams();
  if (filters.name) params.set('name', filters.name);
  if (filters.status) params.set('status', filters.status);
  if (filters.gender) params.set('gender', filters.gender);
  params.set('page', String(page));
  return params.toString();
}

export const charactersApi = rickAndMortyApi.injectEndpoints({
  endpoints: (build) => ({
    // The filters object is the query arg — every filter combination gets its
    // own cache entry, so changing a filter resets pagination for free.
    getCharacters: build.infiniteQuery<Paginated<Character>, CharacterFilters, number>({
      infiniteQueryOptions: {
        initialPageParam: 1,
        getNextPageParam: (lastPage, _allPages, lastPageParam) =>
          getNextPageNumber(lastPage, lastPageParam),
      },
      query: ({ queryArg, pageParam }) => `/character?${toSearchParams(queryArg, pageParam)}`,
    }),

    getCharacter: build.query<Character, number>({
      query: (id) => `/character/${id}`,
    }),

    getCharactersByIds: build.query<Character[], number[]>({
      query: (ids) => `/character/${ids.join(',')}`,
      transformResponse: (response: Character | Character[]) => normalizeToArray(response),
    }),
  }),
});

export const { useGetCharactersInfiniteQuery, useGetCharacterQuery, useGetCharactersByIdsQuery } =
  charactersApi;
