import React from 'react';

import './MagicCardSetDetails.css';

/**
 * @typedef MagicCardProps
 * @property {string} name
 * @property {string} imgUrl
 * @property {number} year
 * @property {number} cardCount
 *
 * @param {MagicCardProps} props
 * @returns {React.ReactElement}
 */
const MagicCardSetDetails = ({ name, imgUrl, cardCount, year }) => {
  return (
    <div className="magicCardSetDetails">
      <img className="image" src={imgUrl} alt={name} />
      <h3 className="name">{name}</h3>
      <h4 className="year">Released {year}</h4>
      <h4 className="cardCount">
        {cardCount === 1 ? '1 Card' : `${cardCount} Cards`}
      </h4>
    </div>
  );
};

export { MagicCardSetDetails };
