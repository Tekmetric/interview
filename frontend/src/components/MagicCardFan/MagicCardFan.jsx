import React from 'react';

import './MagicCardFan.css';

const MagicCardFan = ({ children }) => {
  const totalCards = React.Children.count(children);

  return (
    <div className="magicCardFan" style={{ '--total-cards': totalCards }}>
      {React.Children.map(children, (child, index) =>
        React.cloneElement(child, {
          style: { '--index': index - (children.length - 1) / 2 },
        }),
      )}
    </div>
  );
};

export { MagicCardFan };
