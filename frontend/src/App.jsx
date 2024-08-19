import React, { useEffect, useRef, useState } from 'react';
import { useBootstrap } from './hooks/useBootstrap';

import './App.css';

const App = () => {
  const cardSetRef = useRef(null);
  const [cardSetCode, setCardSetCode] = useState('');
  const { isBootstrapping, bootstrapError, cardSets } = useBootstrap();

  useEffect(
    () => {
      const handleBootstrapCompleted = () => {
        if (cardSets.length > 0) {
          setCardSetCode(cardSets[0].code);
          if (cardSetRef.current) {
            cardSetRef.current.focus();
          }
        }
      };

      if (!isBootstrapping && !bootstrapError) {
        handleBootstrapCompleted();
      }
    },
    [isBootstrapping, bootstrapError],
  );

  const handleCardSetChange = e => {
    const newCardSetCode = e.target.value;
    if (newCardSetCode !== cardSetCode) {
      setSelectedCardIndex(undefined);
      setCardSetCode(newCardSetCode);
    }
  };

  if (isBootstrapping) {
    return <div>Bootstrapping the app...</div>;
  }

  if (bootstrapError) {
    return <div>Error: {bootstrapError.message}</div>;
  }

  return (
    <div className="app">
      <main className="pageWrapper">
        <h1 className="title">Magic The Gathering Booster Pack Generator</h1>
        <div className="content">
          <div className="leftSide">
            <div>
              <label htmlFor="cardSet">Set: </label>
              <select
                id="cardSet"
                ref={cardSetRef}
                onChange={handleCardSetChange}
                value={cardSetCode}
              >
                {cardSets.map(cardSet => (
                  <option key={cardSet.code} value={cardSet.code}>
                    {cardSet.name}
                  </option>
                ))}
              </select>
            </div>
          </div>
          <div className="rightSide" />
        </div>
      </main>
    </div>
  );
};

export default App;
