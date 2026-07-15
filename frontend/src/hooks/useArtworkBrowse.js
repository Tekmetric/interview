import { useCallback, useEffect, useRef, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { useDebouncedValue } from './useDebouncedValue';
import { useDepartments } from './useDepartments';
import { useArtworkSearch } from './useArtworkSearch';
import { usePagedArtworks } from './usePagedArtworks';
import { STATUS } from '../lib/status';
import { MIN_QUERY_LENGTH, SEARCH_DEBOUNCE_MS } from '../lib/constants';

// This is the one place that decides *what* to search: a typed term once it's
// long enough, otherwise nothing (the page shows a landing state). The
// search/paging hooks just run whatever query they're handed.
export function useArtworkBrowse() {
  const [searchParams, setSearchParams] = useSearchParams();
  const [query, setQuery] = useState(() => searchParams.get('q') ?? '');
  const [departmentId, setDepartmentId] = useState(() => searchParams.get('dept') ?? '');

  const [debouncedQuery, submit, syncDebounced] = useDebouncedValue(query, SEARCH_DEBOUNCE_MS);
  const trimmedQuery = debouncedQuery.trim();
  const hasQuery = trimmedQuery.length >= MIN_QUERY_LENGTH;
  const activeQuery = hasQuery ? trimmedQuery : '';

  // Latest URL + setter, read (not depended on) by the writer effect below. This
  // matters because react-router recreates setSearchParams on every navigation;
  // if the effect depended on it, a Back press would re-run the writer with
  // still-stale (pre-debounce) state and re-push the query we're leaving —
  // an infinite navigation loop. Keeping them in refs means the writer only fires
  // when the search state itself changes.
  const searchParamsRef = useRef(searchParams);
  searchParamsRef.current = searchParams;
  const setSearchParamsRef = useRef(setSearchParams);
  setSearchParamsRef.current = setSearchParams;

  // State → URL. Each distinct (debounced) query/department is pushed as its own
  // history entry so Back/Forward walks the search history. We skip the write
  // when the URL already matches — that's what breaks the echo loop where our own
  // write, or a value we just adopted from a navigation, would push again.
  useEffect(() => {
    const currentQ = searchParamsRef.current.get('q') ?? '';
    const currentDept = searchParamsRef.current.get('dept') ?? '';
    const nextQ = hasQuery ? trimmedQuery : '';
    if (nextQ === currentQ && departmentId === currentDept) return;

    const next = new URLSearchParams();
    if (nextQ) next.set('q', nextQ);
    if (departmentId) next.set('dept', departmentId);
    setSearchParamsRef.current(next);
  }, [trimmedQuery, hasQuery, departmentId]);

  // URL → state, for back/forward and edited links. syncDebounced settles the
  // debounced value immediately so results reflect the navigation without a
  // 750ms lag (and so the writer effect above sees the matching state and skips).
  useEffect(() => {
    const urlQ = searchParams.get('q') ?? '';
    const urlDept = searchParams.get('dept') ?? '';
    setQuery((cur) => (urlQ !== cur ? urlQ : cur));
    setDepartmentId((cur) => (urlDept !== cur ? urlDept : cur));
    syncDebounced(urlQ);
  }, [searchParams, syncDebounced]);

  // Set the query and search right away, skipping the debounce — for explicit
  // picks (suggestion chips, landing examples) where there's no typing to settle.
  const submitQuery = useCallback(
    (term) => {
      setQuery(term);
      syncDebounced(term);
    },
    [syncDebounced]
  );

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
    submitQuery,
    departmentId,
    setDepartmentId,
    departments,
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
