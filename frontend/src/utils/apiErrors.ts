import type { FetchBaseQueryError } from '@reduxjs/toolkit/query';

// The Rick and Morty API answers a search with no matches with HTTP 404
// instead of an empty result list. For list screens that's an empty state,
// not an error — this predicate lets the UI tell the two apart.
export function isNotFoundError(error: unknown): boolean {
  return (
    typeof error === 'object' &&
    error !== null &&
    'status' in error &&
    (error as FetchBaseQueryError).status === 404
  );
}
