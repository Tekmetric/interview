import { useMemo } from "react";
import useApi from "./useApi";

/**
 * Hook for fetching Pokemon TCG cards from TCGdex API
 * @param {Object} queryParams - Query parameters for filtering cards
 * @param {string} queryParams.name - Filter by card name
 * @param {string} queryParams.set - Filter by set id
 * @returns {Object} { cards, loading, error, refetch }
 */
const usePokemonCards = (queryParams = {}) => {
  const { name, set } = queryParams;

  const url = useMemo(
    () => {
      if (set) {
        return `https://api.tcgdex.net/v2/en/sets/${set}`;
      }

      return "https://api.tcgdex.net/v2/en/cards";
    },
    [set]
  );

  const { data, loading, error, refetch } = useApi(url, {
    immediate: !!set, // Only fetch immediately if a set is selected
    dependencies: [set],
  });

  const processedData = useMemo(
    () => {
      if (!data) {
        return {
          cards: [],
        };
      }

      let cards = [];

      // If we fetched a specific set, extract the cards array
      if (set && data.cards) {
        cards = data.cards;
      }
      // If we fetched all cards, data is already an array
      else if (Array.isArray(data)) {
        cards = data;
      }

      // Filter by name if provided
      if (name && cards.length > 0) {
        const searchTerm = name.toLowerCase();
        cards = cards.filter(
          (card) => card.name && card.name.toLowerCase().includes(searchTerm)
        );
      }

      return {
        cards,
      };
    },
    [data, name, set]
  );

  return {
    cards: processedData.cards,
    loading,
    error,
    refetch,
  };
};

export default usePokemonCards;
