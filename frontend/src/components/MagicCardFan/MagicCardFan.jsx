import React from 'react';
import { MagicCardFanItem } from '../MagicCardFanItem/MagicCardFanItem';

import './MagicCardFan.css';

// We have to use the index too, as a booster pack
// could have a duplicate card in it
const getKey = (card, cardIndex) => `${cardIndex}:${card.id}`;

/**
 * @typedef MagicCardType
 * @property {string} id
 * @property {string} name
 * @property {string} imgUrl
 *
 * @param {{ cards: MagicCardType[] }} props
 * @returns
 */
const MagicCardFan = ({ cards, handleCardClick }) => {
  const totalCardCount = cards.length;

  return (
    <div className="magicCardFan" style={{ '--total-cards': totalCardCount }}>
      {cards.map((card, cardIndex) => (
        <MagicCardFanItem
          key={getKey(card, cardIndex)}
          card={card}
          onClick={() => handleCardClick(cardIndex)}
          style={{
            '--index': cardIndex - (totalCardCount - 1) / 2,
          }}
        />
      ))}
    </div>
  );
};

export { MagicCardFan };
