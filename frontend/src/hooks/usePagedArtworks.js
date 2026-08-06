import { useCallback, useEffect, useRef, useState } from 'react';
import { fetchObject } from '../api/metMuseum';
import { RESULTS_PER_PAGE } from '../lib/constants';
import { STATUS } from '../lib/status';

// Pages through a list of object IDs, fetching each page's details in parallel
// and accumulating them for a "load more" UI. Each load gets a request token;
// a resolution is applied only if it's still the latest, so an in-flight page
// that's been superseded (new search, or rapid load-more) is ignored.
export function usePagedArtworks(ids, pageSize = RESULTS_PER_PAGE) {
  const [items, setItems] = useState([]);
  const [status, setStatus] = useState(STATUS.idle);
  const [loaded, setLoaded] = useState(0);
  const [failedCount, setFailedCount] = useState(0);
  const requestRef = useRef(0);
  const controllerRef = useRef(null);

  const loadFrom = useCallback(
    (from, list) => {
      const slice = list.slice(from, from + pageSize);
      if (slice.length === 0) return;

      const request = (requestRef.current += 1);
      controllerRef.current?.abort();
      const controller = new AbortController();
      controllerRef.current = controller;
      setStatus(STATUS.loading);

      // A single object can 404 or be rate-limited, so each failure resolves to
      // null instead of rejecting the page; we count the misses and surface them.
      Promise.all(
        slice.map((id) => fetchObject(id, controller.signal).catch(() => null))
      ).then((results) => {
        if (request !== requestRef.current) return;
        const loadedItems = results.filter(Boolean);
        const failed = results.length - loadedItems.length;
        setItems((prev) => (from === 0 ? loadedItems : [...prev, ...loadedItems]));
        setLoaded(from + slice.length);
        setFailedCount((prev) => (from === 0 ? failed : prev + failed));
        setStatus(STATUS.success);
      });
    },
    [pageSize]
  );

  useEffect(() => {
    setItems([]);
    setLoaded(0);
    setFailedCount(0);
    if (!ids || ids.length === 0) {
      requestRef.current += 1;
      controllerRef.current?.abort();
      setStatus(STATUS.idle);
      return;
    }
    loadFrom(0, ids);
    return () => controllerRef.current?.abort();
  }, [ids, loadFrom]);

  const hasMore = ids ? loaded < ids.length : false;
  const loadMore = useCallback(
    () => loadFrom(loaded, ids ?? []),
    [loadFrom, loaded, ids]
  );

  return { items, status, failedCount, hasMore, loadMore };
}
