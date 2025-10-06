import React from 'react';
import PropTypes from 'prop-types';

export const useKeyboardNavigation = (listRef, itemCount) => {
  const handleKeyDown = (e) => {
    // Only handle keyboard navigation if this element is focused
    // and not if focus is on an input or link
    const targetTag = e.target.tagName.toLowerCase();
    if (targetTag === 'input' || targetTag === 'a' || targetTag === 'button') {
      return;
    }

    if (!listRef.current) return;

    const currentScrollOffset = listRef.current.state.scrollOffset;

    switch(e.key) {
      case 'ArrowDown':
        e.preventDefault();
        listRef.current.scrollTo(currentScrollOffset + 100);
        break;
      case 'ArrowUp':
        e.preventDefault();
        listRef.current.scrollTo(Math.max(0, currentScrollOffset - 100));
        break;
      case 'PageDown':
        e.preventDefault();
        listRef.current.scrollTo(currentScrollOffset + 400);
        break;
      case 'PageUp':
        e.preventDefault();
        listRef.current.scrollTo(Math.max(0, currentScrollOffset - 400));
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

export const KeyboardNavigationWrapper = ({ listRef, itemCount, className, ariaLabel, children }) => {
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

KeyboardNavigationWrapper.propTypes = {
  listRef: PropTypes.object.isRequired,
  itemCount: PropTypes.number.isRequired,
  className: PropTypes.string,
  ariaLabel: PropTypes.string,
  children: PropTypes.node.isRequired,
};
