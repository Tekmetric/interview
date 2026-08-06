import { createContext, useCallback, useContext, useMemo } from 'react';
import { useLocalStorage } from '../hooks/useLocalStorage';

// Stores the full normalized artwork so the collection page renders without
// re-fetching from the API.
const CollectionContext = createContext(null);

export function CollectionProvider({ children }) {
  const [items, setItems] = useLocalStorage('meetthemet:collection', [], {
    legacyKey: 'artfinder:collection',
  });

  const isSaved = useCallback((id) => items.some((a) => a.id === id), [items]);

  const toggle = useCallback(
    (artwork) => {
      setItems(
        items.some((a) => a.id === artwork.id)
          ? items.filter((a) => a.id !== artwork.id)
          : [artwork, ...items]
      );
    },
    [items, setItems]
  );

  const remove = useCallback(
    (id) => setItems(items.filter((a) => a.id !== id)),
    [items, setItems]
  );

  const clear = useCallback(() => setItems([]), [setItems]);

  const value = useMemo(
    () => ({ items, count: items.length, isSaved, toggle, remove, clear }),
    [items, isSaved, toggle, remove, clear]
  );

  return (
    <CollectionContext.Provider value={value}>
      {children}
    </CollectionContext.Provider>
  );
}

export function useCollection() {
  const ctx = useContext(CollectionContext);
  if (!ctx) {
    throw new Error('useCollection must be used within a CollectionProvider');
  }
  return ctx;
}
