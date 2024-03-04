import { useContext } from 'react';
import { SearchContext } from '../state/SearchContext';

const useSearch = () => {
  const context = useContext(SearchContext);
  if (!context) {
    throw new Error('Context must be not null');
  }

  return context;
};

export default useSearch;
