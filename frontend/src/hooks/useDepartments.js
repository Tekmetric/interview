import { useEffect } from 'react';
import { fetchDepartments } from '../api/metMuseum';
import { useLocalStorage } from './useLocalStorage';

// Departments effectively never change, so we serve a localStorage cache
// immediately and refresh in the background. The API layer handles transient
// retries; on failure we simply keep the cached list.
export function useDepartments() {
  const [departments, setDepartments] = useLocalStorage('meetthemet:departments', [], {
    legacyKey: 'artfinder:departments',
  });

  useEffect(() => {
    const controller = new AbortController();
    fetchDepartments(controller.signal)
      .then((list) => {
        if (list.length) setDepartments(list);
      })
      .catch(() => {
        // Offline or rate-limited past retries — the cached value stands in.
      });
    return () => controller.abort();
  }, [setDepartments]);

  return departments;
}
