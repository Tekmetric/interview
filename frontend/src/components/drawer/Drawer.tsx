import { useEffect, useState, type ReactNode } from 'react';
import { usePrefersReducedMotion } from './usePrefersReducedMotion';
import './drawer.css';

interface DrawerProps {
  isOpen: boolean;
  onClose: () => void;
  title: string;
  titleId: string;
  closeAriaLabel: string;
  children: ReactNode;
  footer?: ReactNode;
  panelClassName?: string;
}

export function Drawer({
  isOpen,
  onClose,
  title,
  titleId,
  closeAriaLabel,
  children,
  footer,
  panelClassName,
}: DrawerProps) {
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
        className={`drawer-backdrop ${isVisible ? 'drawer-backdrop--visible' : 'drawer-backdrop--hidden'}`}
        onClick={onClose}
      />
      <div
        role="dialog"
        aria-modal="true"
        aria-labelledby={titleId}
        onTransitionEnd={handleTransitionEnd}
        className={`drawer-panel ${panelClassName ?? ''} ${isVisible ? 'drawer-panel--visible' : 'drawer-panel--hidden'}`.trim()}
      >
        <div className="drawer-header">
          <h2 id={titleId} className="drawer-title">
            {title}
          </h2>
          <button
            type="button"
            aria-label={closeAriaLabel}
            onClick={onClose}
            className="drawer-close"
          >
            <span aria-hidden="true">&times;</span>
          </button>
        </div>
        <div className="drawer-body">{children}</div>
        {footer && <div className="drawer-footer">{footer}</div>}
      </div>
    </>
  );
}
