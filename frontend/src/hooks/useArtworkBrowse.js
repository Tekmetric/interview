import { useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { useDebouncedValue } from './useDebouncedValue';
import { useDepartments } from './useDepartments';
import { useArtworkSearch } from './useArtworkSearch';
import { usePagedArtworks } from './usePagedArtworks';
import { STATUS } from '../lib/status';
import {
  DEFAULT_QUERY,
  MIN_QUERY_LENGTH,
  SEARCH_DEBOUNCE_MS,
} from '../lib/constants';

// This is the one place that decides *what* to search: a typed term once it's
// long enough, otherwise the featured landing set. The search/paging hooks just
// run whatever query they're handed.
export function useArtworkBrowse() {
  const [searchParams, setSearchParams] = useSearchParams();
  const [query, setQuery] = useState(() => searchParams.get('q') ?? DEFAULT_QUERY);
  const [departmentId, setDepartmentId] = useState(() => searchParams.get('dept') ?? '');

  const [debouncedQuery, submit] = useDebouncedValue(query, SEARCH_DEBOUNCE_MS);
  const trimmedQuery = debouncedQuery.trim();
  const isFeatured = trimmedQuery.length < MIN_QUERY_LENGTH;
  const activeQuery = isFeatured ? DEFAULT_QUERY : trimmedQuery;

  // `replace` so typing doesn't push a history entry per keystroke.
  useEffect(() => {
    const next = new URLSearchParams();
    if (!isFeatured) next.set('q', trimmedQuery);
    if (departmentId) next.set('dept', departmentId);
    setSearchParams(next, { replace: true });
  }, [trimmedQuery, isFeatured, departmentId, setSearchParams]);

  // Adopt external URL changes (back/forward, edited link). Skipping values that
  // already match avoids fighting our own writes above.
  useEffect(() => {
    const urlQ = searchParams.get('q') ?? DEFAULT_QUERY;
    const urlDept = searchParams.get('dept') ?? '';
    setQuery((cur) => (urlQ !== debouncedQuery && urlQ !== cur ? urlQ : cur));
    setDepartmentId((cur) => (urlDept !== cur ? urlDept : cur));
    // eslint-disable-next-line react-hooks/exhaustive-deps -- react only to URL
  }, [searchParams]);

  const departments = useDepartments();
  const search = useArtworkSearch(activeQuery, departmentId || undefined);
  const paged = usePagedArtworks(search.status === STATUS.success ? search.ids : null);

  const initialLoading =
    search.status === STATUS.loading ||
    (paged.status === STATUS.loading && paged.items.length === 0);
  const searchPending =
    query.trim().length > 0 && (query !== debouncedQuery || initialLoading);

  return {
    query,
    setQuery,
    submit,
    departmentId,
    setDepartmentId,
    departments,
    isFeatured,
    searchPending,
    initialLoading,
    status: search.status,
    total: search.total,
    retry: search.retry,
    items: paged.items,
    pagedStatus: paged.status,
    failedCount: paged.failedCount,
    hasMore: paged.hasMore,
    loadMore: paged.loadMore,
  };
}
