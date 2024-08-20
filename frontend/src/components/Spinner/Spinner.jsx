import React from 'react';

import './Spinner.css';

/**
 * *** Why Use isHidden At All? ***
 *
 * This is useful when you ALWAYS want the spinner
 * to remain in the DOM. Having the component disappear
 * from the DOM can lead to unexpected alignment
 * of sibling elements, and making it invisible
 * is faster because you're only making CSS changes
 * via DOM attributes instead of modifying DOM structure.
 * @param {{ isHidden: boolean }} props
 * @returns
 */
const Spinner = ({ isHidden = false }) => {
  return (
    <div className={`spinner ${isHidden ? 'hidden' : ''}`}>
      <span className="ring" />
    </div>
  );
};

export { Spinner };
