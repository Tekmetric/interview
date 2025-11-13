import { useMemo } from "react";
import useApi from "./useApi";

/**
 * Hook for fetching all Pokemon TCG sets from TCGdex API
 * Used for populating dropdown selectors
 * @returns {Object} { sets, loading, error, refetch }
 */
const usePokemonSets = () => {
  const url = "https://api.tcgdex.net/v2/en/sets";

  const { data, loading, error, refetch } = useApi(url);

  // Process and return the data in a more convenient format
  // TCGdex returns an array directly, not wrapped in a data object
  const processedData = useMemo(
    () => {
      if (!data || !Array.isArray(data)) {
        return {
          sets: [],
        };
      }

      // Sort sets by name for better UX (TCGdex doesn't have releaseDate in list view)
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
