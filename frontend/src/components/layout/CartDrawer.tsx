import { useEffect, useState } from 'react';

interface CartDrawerProps {
  isOpen: boolean;
  onClose: () => void;
}

// pop out/in is cognizant of user preferences for reduced motion
function usePrefersReducedMotion() {
  const [prefersReducedMotion, setPrefersReducedMotion] = useState(
    () =>
      typeof window !== 'undefined' &&
      window.matchMedia('(prefers-reduced-motion: reduce)').matches
  );

  useEffect(() => {
    const mediaQuery = window.matchMedia('(prefers-reduced-motion: reduce)');

    function handleChange() {
      setPrefersReducedMotion(mediaQuery.matches);
    }

    mediaQuery.addEventListener('change', handleChange);

    return () => {
      mediaQuery.removeEventListener('change', handleChange);
    };
  }, []);

  return prefersReducedMotion;
}

export function CartDrawer({ isOpen, onClose }: CartDrawerProps) {
  const prefersReducedMotion = usePrefersReducedMotion();
  const [isMounted, setIsMounted] = useState(false);
  const [isVisible, setIsVisible] = useState(false);

  useEffect(() => {
    if (isOpen) {
      setIsMounted(true);

      if (prefersReducedMotion) {
        setIsVisible(true);
        return;
      }

      const frame = requestAnimationFrame(() => {
        requestAnimationFrame(() => setIsVisible(true));
      });
      return () => cancelAnimationFrame(frame);
    }

    setIsVisible(false);

    if (prefersReducedMotion) {
      setIsMounted(false);
    }
  }, [isOpen, prefersReducedMotion]);

  useEffect(() => {
    if (!isMounted) {
      return;
    }

    function handleKeyDown(event: KeyboardEvent) {
      if (event.key === 'Escape') {
        onClose();
      }
    }

    document.addEventListener('keydown', handleKeyDown);

    return () => {
      document.removeEventListener('keydown', handleKeyDown);
    };
  }, [isMounted, onClose]);

  function handleTransitionEnd() {
    if (prefersReducedMotion) {
      return;
    }

    if (!isVisible && !isOpen) {
      setIsMounted(false);
    }
  }

  if (!isMounted) {
    return null;
  }

  return (
    <>
      <div
        aria-hidden="true"
        className={[
          'fixed inset-0 z-40 bg-black/50',
          'transition-opacity duration-300 ease-in-out motion-reduce:transition-none',
          isVisible ? 'opacity-100' : 'opacity-0',
        ].join(' ')}
        onClick={onClose}
      />
      <div
        role="dialog"
        aria-modal="true"
        aria-labelledby="cart-drawer-title"
        onTransitionEnd={handleTransitionEnd}
        className={[
          'fixed z-50 flex flex-col bg-white shadow-xl',
          'transition-transform duration-300 ease-in-out motion-reduce:transition-none',
          'bottom-0 left-0 right-0 h-[50vh] rounded-t-lg',
          'md:bottom-auto md:left-auto md:right-0 md:top-0 md:h-full md:w-80 md:max-w-[90vw] md:rounded-none',
          isVisible
            ? 'translate-y-0 md:translate-x-0'
            : 'translate-y-full md:translate-y-0 md:translate-x-full',
        ].join(' ')}
      >
        <div className="flex items-center justify-between border-b border-neutral-200 px-4 py-3">
          <h2 id="cart-drawer-title" className="text-lg font-semibold text-neutral-900">
            Cart
          </h2>
          <button
            type="button"
            aria-label="Close cart"
            onClick={onClose}
            className="cursor-pointer rounded p-1 text-neutral-500 hover:bg-neutral-100 hover:text-neutral-700 focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue-600"
          >
            <span aria-hidden="true" className="text-xl leading-none">
              &times;
            </span>
          </button>
        </div>
        <div className="flex-1 overflow-y-auto p-4">
          <p className="text-sm text-neutral-600">Cart is empty</p>
        </div>
      </div>
    </>
  );
}
