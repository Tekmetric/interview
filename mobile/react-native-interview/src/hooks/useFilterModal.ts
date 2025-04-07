import { useState, useCallback } from 'react';

export function useFilterModal() {
  const [showFilterModal, setShowFilterModal] = useState(false);

  const toggleFilterModal = useCallback(() => {
    setShowFilterModal((value) => !value);
  }, []);

  const closeFilterModal = useCallback(() => {
    setShowFilterModal(false);
  }, []);

  return {
    showFilterModal,
    toggleFilterModal,
    closeFilterModal,
  };
}
