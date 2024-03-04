import { useContext } from 'react';
import { FavouritesContext } from '../state/FavoritesContext';

const useFavourites = () => {
  const context = useContext(FavouritesContext);
  if (!context) {
    throw new Error('Context must be not null');
  }

  return context;
};

export default useFavourites;
