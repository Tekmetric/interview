import { useEffect } from 'react';

// Prevents the page behind a modal from scrolling, restoring the previous value
// on unmount (so stacked modals hand the lock back correctly).
export function useBodyScrollLock() {
  useEffect(() => {
    const previous = document.body.style.overflow;
    document.body.style.overflow = 'hidden';
    return () => {
      document.body.style.overflow = previous;
    };
  }, []);
}
