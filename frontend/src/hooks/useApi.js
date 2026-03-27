import { useState, useEffect, useCallback } from "react";

/**
 * Generic API hook for making HTTP requests with loading states and error handling
 * @param {string} url - The URL to fetch from
 * @param {Object} options - Configuration options
 * @param {boolean} options.immediate - Whether to fetch immediately on mount (default: true)
 * @param {Array} options.dependencies - Dependencies array for re-fetching
 * @returns {Object} { data, loading, error, refetch }
 */
const useApi = (url, options = {}) => {
  const { immediate = true, dependencies = [] } = options;

  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchData = useCallback(
    async () => {
      if (!url) return;

      setLoading(true);
      setError(null);

      try {
        const defaultOptions = {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
          },
        };

        const response = await fetch(url, defaultOptions);

        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }

        const result = await response.json();
        setData(result);
      } catch (err) {
        setError(err.message || "An error occurred while fetching data");
        console.error("API Error:", err);
      } finally {
        setLoading(false);
      }
    },
    [url]
  );

  // Effect for immediate fetching and dependency-based re-fetching
  useEffect(
    () => {
      if (immediate) {
        fetchData();
      }
    },
    [fetchData, immediate, ...dependencies]
  );

  const refetch = useCallback(
    () => {
      fetchData();
    },
    [fetchData]
  );

  return {
    data,
    loading,
    error,
    refetch,
  };
};

export default useApi;
