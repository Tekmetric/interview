import React from 'react';
import { VariableSizeList } from 'react-window';

// Keyboard navigation scroll distances
const SCROLL_STEP = 100; // Arrow key scroll amount in pixels
const SCROLL_PAGE = 400; // Page up/down scroll amount in pixels

/**
 * Internal react-window state type
 * This is not exported by react-window, but needed for keyboard navigation
 * Using a type alias to avoid interface extension issues
 */
type VariableSizeListWithState = VariableSizeList & {
  state?: {
    scrollOffset?: number;
  };
};

export const useKeyboardNavigation = (
  listRef: React.RefObject<VariableSizeList | null>,
  itemCount: number
) => {
  const handleKeyDown = (e: React.KeyboardEvent<HTMLDivElement>) => {
    // Only handle keyboard navigation if this element is focused
    // and not if focus is on an input or link
    const targetTag = (e.target as HTMLElement).tagName.toLowerCase();
    if (targetTag === 'input' || targetTag === 'a' || targetTag === 'button') {
      return;
    }

    if (!listRef.current) return;

    // Access scrollOffset from the internal state
    const internalList = listRef.current as unknown as VariableSizeListWithState;
    const currentScrollOffset = internalList.state?.scrollOffset || 0;

    switch(e.key) {
      case 'ArrowDown':
        e.preventDefault();
        listRef.current.scrollTo(currentScrollOffset + SCROLL_STEP);
        break;
      case 'ArrowUp':
        e.preventDefault();
        listRef.current.scrollTo(Math.max(0, currentScrollOffset - SCROLL_STEP));
        break;
      case 'PageDown':
        e.preventDefault();
        listRef.current.scrollTo(currentScrollOffset + SCROLL_PAGE);
        break;
      case 'PageUp':
        e.preventDefault();
        listRef.current.scrollTo(Math.max(0, currentScrollOffset - SCROLL_PAGE));
        break;
      case 'Home':
        e.preventDefault();
        listRef.current.scrollToItem(0, 'start');
        break;
      case 'End':
        e.preventDefault();
        listRef.current.scrollToItem(itemCount - 1, 'end');
        break;
      default:
        break;
    }
  };

  return handleKeyDown;
};

interface KeyboardNavigationWrapperProps {
  listRef: React.RefObject<VariableSizeList | null>;
  itemCount: number;
  className?: string;
  ariaLabel?: string;
  children: React.ReactNode;
}

export const KeyboardNavigationWrapper: React.FC<KeyboardNavigationWrapperProps> = ({
  listRef,
  itemCount,
  className,
  ariaLabel,
  children
}) => {
  const handleKeyDown = useKeyboardNavigation(listRef, itemCount);

  return (
    <div
      onKeyDown={handleKeyDown}
      tabIndex={0}
      role="region"
      aria-label={ariaLabel}
      className={className}
    >
      {children}
    </div>
  );
};
