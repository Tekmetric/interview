import { useEffect, useState } from 'react';
import './cartDrawer.css';

interface CartDrawerProps {
  isOpen: boolean;
  onClose: () => void;
}

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
        className={`cart-drawer-backdrop ${isVisible ? 'cart-drawer-backdrop--visible' : 'cart-drawer-backdrop--hidden'}`}
        onClick={onClose}
      />
      <div
        role="dialog"
        aria-modal="true"
        aria-labelledby="cart-drawer-title"
        onTransitionEnd={handleTransitionEnd}
        className={`cart-drawer-panel ${isVisible ? 'cart-drawer-panel--visible' : 'cart-drawer-panel--hidden'}`}
      >
        <div className="cart-drawer-header">
          <h2 id="cart-drawer-title" className="cart-drawer-title">
            Cart
          </h2>
          <button
            type="button"
            aria-label="Close cart"
            onClick={onClose}
            className="cart-drawer-close"
          >
            <span aria-hidden="true">&times;</span>
          </button>
        </div>
        <div className="cart-drawer-body">
          <p className="cart-drawer-empty">Cart is empty</p>
        </div>
      </div>
    </>
  );
}
