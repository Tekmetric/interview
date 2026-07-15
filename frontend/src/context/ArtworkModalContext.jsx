import { createContext, useCallback, useContext, useMemo, useState } from 'react';
import ArtworkModal from '../features/artwork/ArtworkModal';

// A single shared modal instance so any page can open artwork detail without
// managing its own selection state + render.
const ArtworkModalContext = createContext(null);

export function ArtworkModalProvider({ children }) {
  const [selected, setSelected] = useState(null);

  const open = useCallback((artwork) => setSelected(artwork), []);
  const close = useCallback(() => setSelected(null), []);
  const value = useMemo(() => ({ open, close }), [open, close]);

  return (
    <ArtworkModalContext.Provider value={value}>
      {children}
      {selected && <ArtworkModal artwork={selected} onClose={close} />}
    </ArtworkModalContext.Provider>
  );
}

export function useArtworkModal() {
  const ctx = useContext(ArtworkModalContext);
  if (!ctx) {
    throw new Error('useArtworkModal must be used within an ArtworkModalProvider');
  }
  return ctx;
}
