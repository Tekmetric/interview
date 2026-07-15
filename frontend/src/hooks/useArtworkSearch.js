import { useCallback, useEffect, useState } from 'react';
import { searchObjects } from '../api/metMuseum';
import { STATUS } from '../lib/status';

const IDLE = { status: STATUS.idle, ids: [], total: 0, error: null };

// Searches whatever non-empty query it's given; the caller decides what to
// search (typed term vs. featured default). Stale requests are aborted on
// cleanup so a slow earlier search can't overwrite a newer one.
export function useArtworkSearch(query, departmentId) {
  const [state, setState] = useState(IDLE);
  const [reloadKey, setReloadKey] = useState(0);
  const retry = useCallback(() => setReloadKey((k) => k + 1), []);

  useEffect(() => {
    const q = query?.trim() ?? '';
    if (!q) {
      setState(IDLE);
      return;
    }

    const controller = new AbortController();
    setState({ ...IDLE, status: STATUS.loading });

    searchObjects({ query: q, departmentId, signal: controller.signal })
      .then(({ total, ids }) =>
        setState({ status: STATUS.success, ids, total, error: null })
      )
      .catch((err) => {
        if (err.name === 'AbortError') return;
        setState({ status: STATUS.error, ids: [], total: 0, error: err });
      });

    return () => controller.abort();
  }, [query, departmentId, reloadKey]);

  return { ...state, retry };
}
