import React from 'react';
import { capitalize } from '../../lib/string';
import { dollarize } from '../../lib/currency';

import './MagicCardDetails.css';

/**
 * @typedef MagicCardProps
 * @property {string} name
 * @property {string} type
 * @property {string} rarity
 * @property {string} price
 * @property {Function} [onClick]
 *
 * @param {MagicCardProps} props
 * @returns {React.ReactElement}
 */
const MagicCardDetails = ({
  name,
  type,
  rarity,
  price,
  onClick = undefined,
}) => {
  return (
    <div className="magicCardDetails" onClick={onClick}>
      <h2 className="name">{name}</h2>
      <h3 className="type">{type}</h3>
      <h5 className="rarityAndPrice">
        {capitalize(rarity)}, {dollarize(price)}
      </h5>
    </div>
  );
};

export { MagicCardDetails };
