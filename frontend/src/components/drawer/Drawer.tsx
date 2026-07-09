import { useEffect, useRef, useState, type ReactNode } from 'react';
import { createPortal } from 'react-dom';
import { usePrefersReducedMotion } from '../../hooks/usePrefersReducedMotion';
import './drawer.css';

const FOCUSABLE_SELECTORS = [
  'a[href]',
  'button:not([disabled])',
  'input:not([disabled])',
  'select:not([disabled])',
  'textarea:not([disabled])',
  '[tabindex]:not([tabindex="-1"])',
].join(', ');

function getFocusableElements(container: HTMLElement): HTMLElement[] {
  return Array.from(
    container.querySelectorAll<HTMLElement>(FOCUSABLE_SELECTORS)
  ).filter((element) => !element.hasAttribute('disabled'));
}

interface DrawerProps {
  isOpen: boolean;
  onClose: () => void;
  title: string;
  titleId: string;
  panelId: string;
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
  panelId,
  closeAriaLabel,
  children,
  footer,
  panelClassName,
}: DrawerProps) {
  const prefersReducedMotion = usePrefersReducedMotion();
  const panelRef = useRef<HTMLDivElement>(null);
  const previousActiveElementRef = useRef<HTMLElement | null>(null);
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

    const root = document.getElementById('root');
    const previousOverflow = document.body.style.overflow;
    const previousRootInert = root?.inert ?? false;

    document.body.style.overflow = 'hidden';
    if (root) {
      root.inert = true;
    }

    return () => {
      document.body.style.overflow = previousOverflow;
      if (root) {
        root.inert = previousRootInert;
      }
    };
  }, [isMounted]);

  useEffect(() => {
    if (!isMounted) {
      return;
    }

    previousActiveElementRef.current = document.activeElement as HTMLElement | null;

    const panel = panelRef.current;
    if (!panel) {
      return;
    }

    requestAnimationFrame(() => {
      const focusableElements = getFocusableElements(panel);
      const target =
        focusableElements[0] ??
        panel.querySelector<HTMLElement>('.drawer-close');
      target?.focus();
    });

    function handleKeyDown(event: KeyboardEvent) {
      const activePanel = panelRef.current;
      if (!activePanel) {
        return;
      }

      if (event.key === 'Escape') {
        onClose();
        return;
      }

      if (event.key !== 'Tab') {
        return;
      }

      const focusableElements = getFocusableElements(activePanel);
      if (focusableElements.length === 0) {
        return;
      }

      const first = focusableElements[0];
      const last = focusableElements[focusableElements.length - 1];
      if (!first || !last) {
        return;
      }

      const active = document.activeElement;

      if (event.shiftKey) {
        if (active === first || !activePanel.contains(active)) {
          event.preventDefault();
          last.focus();
        }
        return;
      }

      if (active === last || !activePanel.contains(active)) {
        event.preventDefault();
        first.focus();
      }
    }

    document.addEventListener('keydown', handleKeyDown);

    return () => {
      document.removeEventListener('keydown', handleKeyDown);
      previousActiveElementRef.current?.focus();
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

  return createPortal(
    <>
      <div
        aria-hidden="true"
        className={`drawer-backdrop ${isVisible ? 'drawer-backdrop--visible' : 'drawer-backdrop--hidden'}`}
        onClick={onClose}
      />
      <div
        ref={panelRef}
        id={panelId}
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
    </>,
    document.body
  );
}
