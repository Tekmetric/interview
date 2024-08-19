import React from 'react';

import './MagicCardLarge.css';

const MagicCardLarge = ({ imgUrl, name }) => {
  return <img className="magicCardLarge" src={imgUrl} alt={name} />;
};

export { MagicCardLarge };
