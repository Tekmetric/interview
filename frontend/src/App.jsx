import React, { useEffect, useMemo, useRef, useState } from 'react';
import { MagicCardSetDetails } from './components/MagicCardSetDetails/MagicCardSetDetails';
import { useBootstrap } from './hooks/useBootstrap';
import { useGetBooster } from './hooks/useGetBooster';

import './App.css';

const App = () => {
  const cardSetRef = useRef(null);
  const [cardSetCode, setCardSetCode] = useState('');
  const { getBoosterStatus, generateBooster } = useGetBooster();
  const { isBootstrapping, bootstrapError, cardSets } = useBootstrap();
  const isGeneratingBooster = getBoosterStatus !== '';
  const selectedCardSet = useMemo(
    () => {
      if (!Array.isArray(cardSets) || !cardSetCode) {
        return null;
      }
      return cardSets.find(cardSet => cardSet.code === cardSetCode);
    },
    [cardSets, cardSetCode],
  );

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

  const handleGetBooster = () => {
    generateBooster(cardSetCode);
  };

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
                disabled={isGeneratingBooster}
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
            <button disabled={getBoosterStatus} onClick={handleGetBooster}>
              Generate Booster
            </button>
          </div>
          <div className="rightSide">
            {selectedCardSet && (
              <MagicCardSetDetails
                name={selectedCardSet.name}
                imgUrl={selectedCardSet.imgUrl}
                cardCount={selectedCardSet.cardCount}
                year={selectedCardSet.year}
              />
            )}
          </div>
        </div>
      </main>
    </div>
  );
};

export default App;
