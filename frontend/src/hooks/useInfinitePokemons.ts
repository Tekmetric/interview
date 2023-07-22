import { useInfiniteQuery } from '@tanstack/react-query';
import { fetchPokemons } from '../api/fetchPokemons';

export const DEFAULT_PAGE_SIZE: number = 5;
export const DEFAULT_OFFSET_SIZE: number = 5;

export function useInfinitePokemons() {
  return useInfiniteQuery({
    queryKey: ['pokemons'],
    queryFn: ({ pageParam = 0 }) =>
      fetchPokemons({
        offset: DEFAULT_OFFSET_SIZE * pageParam,
        limit: DEFAULT_PAGE_SIZE,
      }),
    getNextPageParam: (lastPage, allPages) =>
      lastPage.results.length === 0 ? undefined : allPages.length + 1,
  });
}
