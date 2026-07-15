import { useEffect } from 'react';
import { fetchDepartments } from '../api/metMuseum';
import { useLocalStorage } from './useLocalStorage';

// Departments effectively never change and the API rate-limits under load, so we
// serve a localStorage cache immediately and refresh in the background, retrying
// with backoff and only overwriting the cache on success.
export function useDepartments() {
  const [departments, setDepartments] = useLocalStorage('artfinder:departments', []);

  useEffect(() => {
    const controller = new AbortController();
    let cancelled = false;

    async function load() {
      for (let attempt = 0; attempt < 4 && !cancelled; attempt += 1) {
        try {
          const list = await fetchDepartments(controller.signal);
          if (cancelled) return;
          if (list.length) {
            setDepartments(list);
            return;
          }
        } catch (err) {
          if (err.name === 'AbortError') return;
        }
        // Back off before retrying (0.6s, 1.2s, 1.8s).
        await new Promise((resolve) => setTimeout(resolve, 600 * (attempt + 1)));
      }
    }

    load();
    return () => {
      cancelled = true;
      controller.abort();
    };
  }, [setDepartments]);

  return departments;
}
