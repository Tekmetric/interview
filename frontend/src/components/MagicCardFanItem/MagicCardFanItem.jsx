import React from 'react';

import './MagicCardFanItem.css';

/**
 * @typedef MagicCardType
 * @property {string} id
 * @property {string} name
 * @property {string} imgUrl
 *
 * @typedef MagicCardFanItemProps
 * @property {MagicCardType} card
 * @property {Function} [onClick]
 * @property {React.StyleHTMLAttributes} [style]
 *
 * @param {MagicCardFanItemProps} props
 * @returns {React.ReactElement}
 */
const MagicCardFanItem = ({ card, onClick = undefined, style = {} }) => {
  const { name, imgUrl } = card;

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
