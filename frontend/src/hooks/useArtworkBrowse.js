import { useCallback, useEffect, useRef, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { useDebouncedValue } from './useDebouncedValue';
import { useDepartments } from './useDepartments';
import { useArtworkSearch } from './useArtworkSearch';
import { usePagedArtworks } from './usePagedArtworks';
import { STATUS } from '../lib/status';
import { MIN_QUERY_LENGTH, SEARCH_DEBOUNCE_MS } from '../lib/constants';

// The URL (?q, ?dept) is the source of truth for the committed search, so
// Back/Forward and shareable links work for free. The text field is the only
// local state: it commits to the URL once typing settles, and follows the URL on
// any *external* change (Back/Forward, a nav link, a hand-edited URL) while
// ignoring our own writes — so navigation resets the field without clobbering
// in-flight typing.
export function useArtworkBrowse() {
  const [searchParams, setSearchParams] = useSearchParams();

  const committedQuery = (searchParams.get('q') ?? '').trim();
  const departmentId = searchParams.get('dept') ?? '';

  const [input, setInput] = useState(committedQuery);
  const [debouncedInput, flush] = useDebouncedValue(input, SEARCH_DEBOUNCE_MS);

  // The last query we pushed to the URL. If the URL's q ever differs from this,
  // the change came from outside this hook (navigation) and the field follows it.
  const lastWrittenQuery = useRef(committedQuery);

  // Functional update reads the freshest params, avoiding a stale-closure fight
  // with a concurrent navigation.
  const writeParams = useCallback(
    (mutate) => {
      setSearchParams((prev) => {
        const next = new URLSearchParams(prev);
        mutate(next);
        return next;
      });
    },
    [setSearchParams]
  );

  const writeQuery = useCallback(
    (nextQ) => {
      lastWrittenQuery.current = nextQ;
      writeParams((p) => (nextQ ? p.set('q', nextQ) : p.delete('q')));
    },
    [writeParams]
  );

  const setDepartmentId = useCallback(
    (id) => writeParams((p) => (id ? p.set('dept', id) : p.delete('dept'))),
    [writeParams]
  );

  // Explicit picks (landing examples): fill the field and commit now, no debounce.
  const submitQuery = useCallback(
    (term) => {
      const trimmed = term.trim();
      setInput(term);
      writeQuery(trimmed.length >= MIN_QUERY_LENGTH ? trimmed : '');
    },
    [writeQuery]
  );

  // Commit the settled input to the URL (one history entry per distinct search).
  // Depends only on `debouncedInput`: reacting to `committedQuery` too would let a
  // navigation re-commit the stale input and undo itself.
  useEffect(() => {
    const trimmed = debouncedInput.trim();
    const nextQ = trimmed.length >= MIN_QUERY_LENGTH ? trimmed : '';
    if (nextQ !== committedQuery) writeQuery(nextQ);
    // eslint-disable-next-line react-hooks/exhaustive-deps -- commit only when the debounce settles
  }, [debouncedInput]);

  // Follow the URL into the field when the change wasn't ours (Back/Forward, a nav
  // link back to the empty state, a hand-edited URL).
  useEffect(() => {
    if (committedQuery !== lastWrittenQuery.current) {
      lastWrittenQuery.current = committedQuery;
      setInput(committedQuery);
    }
  }, [committedQuery]);

  const departments = useDepartments();
  const search = useArtworkSearch(committedQuery, departmentId || undefined);
  const paged = usePagedArtworks(search.status === STATUS.success ? search.ids : null);

  const initialLoading =
    search.status === STATUS.loading ||
    (paged.status === STATUS.loading && paged.items.length === 0);
  // Pending when there's a committable query the URL hasn't caught up to yet, or
  // while the committed search is loading. Deliberately not `input !==
  // debouncedInput`: that lingers for the full debounce after a Back navigation,
  // showing a spinner over results that are already correct.
  const trimmedInput = input.trim();
  const searchPending =
    (trimmedInput.length >= MIN_QUERY_LENGTH && trimmedInput !== committedQuery) ||
    initialLoading;

  return {
    query: input,
    setQuery: setInput,
    submit: flush,
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
