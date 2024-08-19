import React from 'react';

import './MagicCard.css';

/**
 * @typedef MagicCardProps
 * @property {string} name
 * @property {string} imgUrl
 * @property {Function} onClick
 * @property {React.StyleHTMLAttributes} [style]
 *
 * @param {MagicCardProps} props
 * @returns {React.ReactElement}
 */
const MagicCard = ({ imgUrl, name, onClick = undefined, style = {} }) => {
  return (
    <img
      src={imgUrl}
      alt={name}
      className="magicCard"
      style={{ ...style }}
      onClick={onClick}
    />
  );
};

export { MagicCard };
