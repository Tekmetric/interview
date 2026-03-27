import { useMemo } from "react";
import useApi from "./useApi";

/**
 * Hook for fetching detailed information about a specific Pokemon card from TCGdex API
 * @param {string} cardId - The unique ID of the card to fetch (e.g., "base1-4")
 * @param {Object} options - Configuration options
 * @param {boolean} options.enabled - Whether to fetch the card details (default: true)
 * @returns {Object} { card, loading, error, refetch }
 */
const usePokemonCardDetails = (cardId, options = {}) => {
  const { enabled = true } = options;

  const url = useMemo(
    () => {
      if (!cardId) return null;
      return `https://api.tcgdex.net/v2/en/cards/${cardId}`;
    },
    [cardId]
  );

  const { data, loading, error, refetch } = useApi(url, {
    immediate: enabled && !!cardId,
    dependencies: [cardId, enabled],
  });

  return {
    card: data || null,
    loading,
    error,
    refetch,
  };
};

export default usePokemonCardDetails;
