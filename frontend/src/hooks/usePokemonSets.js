import { useMemo } from "react";
import useApi from "./useApi";

/**
 * Hook for fetching all Pokemon TCG sets from TCGdex API
 * @returns {Object} { sets, loading, error, refetch }
 */
const usePokemonSets = () => {
  const url = "https://api.tcgdex.net/v2/en/sets";

  const { data, loading, error, refetch } = useApi(url);

  // TCGdex returns an array directly, not wrapped in a data object
  const processedData = useMemo(
    () => {
      if (!data || !Array.isArray(data)) {
        return {
          sets: [],
        };
      }

      const sortedSets = [...data].sort((a, b) => a.name.localeCompare(b.name));

      return {
        sets: sortedSets,
      };
    },
    [data]
  );

  return {
    sets: processedData.sets,
    loading,
    error,
    refetch,
  };
};

export default usePokemonSets;
