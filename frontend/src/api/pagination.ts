import type { Paginated } from './types';

// Shared by every paginated endpoint. The API signals the last page with
// info.next === null; returning undefined then flips the infinite-query
// hook's hasNextPage to false and the Load More button disappears.
export function getNextPageNumber<T>(
  lastPage: Paginated<T>,
  lastPageParam: number,
): number | undefined {
  return lastPage.info.next ? lastPageParam + 1 : undefined;
}
