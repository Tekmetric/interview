import { useState } from 'react';

import api from '../api';

export const useFetchCharacters = () => {
  const [isLoading, setIsLoading] = useState(false);
  const [hasError, setHasError] = useState(false);

  const fetchCharacters = async (name, page) => {
    let data = null;
    try {
      setIsLoading(true);
      setHasError(false);
      const response = await api.fetchCharacters(name, page);
      data = {
        pageCount: response.info.pages,
        characterTotal: response.info.count,
        characterCount: response.results.length,
        characters: response.results,
      };
    } catch (err) {
      setHasError(true);
    } finally {
      setIsLoading(false);
    }
    return data;
  };

  return {
    isLoading,
    hasError,
    fetchCharacters,
  };
};
