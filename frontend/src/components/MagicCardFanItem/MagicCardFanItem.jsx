import React from 'react';

import './MagicCardFanItem.css';

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
const MagicCardFanItem = ({
  imgUrl,
  name,
  onClick = undefined,
  style = {},
}) => {
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

export { MagicCardFanItem };
