import { useState, useCallback } from 'react';
import { fetchCardsFromCardSet } from '../api/fetchCardsFromCardSet';

// The most amount of sets that should be stored in the card cache
const MAX_CARD_SET_CACHE_SIZE = 5;

const MYTHIC_CHANCE = 0.125; // 1:8 chance of getting a Mythic instead of a Rare
const getRareOrMythicRarity = () => {
  if (Math.random() < MYTHIC_CHANCE) {
    return 'mythic';
  } else {
    return 'rare';
  }
};

const UNCOMMON_COUNT = 3;
const RARE_OR_MYTHIC_COUNT = 1;
const COMMON_COUNT = 11;
const getBoosterRarities = () =>
  new Array(UNCOMMON_COUNT)
    .fill('uncommon')
    .concat(new Array(RARE_OR_MYTHIC_COUNT).fill(getRareOrMythicRarity()))
    .concat(new Array(COMMON_COUNT).fill('common'));

const cardSetCardsCache = new Map();
const cardSetCardsCacheAge = [];
const fetchAndCacheCardSet = async cardSetCode => {
  // If we're maxed on cache size, remove the oldest entry
  if (cardSetCardsCacheAge.length === MAX_CARD_SET_CACHE_SIZE) {
    // Remove the oldest set of cards
    const cardSetCodeToExpire = cardSetCardsCacheAge.shift();
    cardSetCardsCache.delete(cardSetCodeToExpire);
  }
  const fetchedCards = await fetchCardsFromCardSet(cardSetCode);
  // Add the newly-fetched cards to the cache
  cardSetCardsCache.set(cardSetCode, fetchedCards);
  cardSetCardsCacheAge.push(cardSetCode);
};

const useGetBooster = () => {
  const [cards, setCards] = useState(null);
  const [generatedCardSetCode, setGeneratedCardSetCode] = useState('');
  const [getBoosterStatus, setGetBoosterStatus] = useState('');
  const [error, setError] = useState(null);

  const generateBooster = useCallback(
    async cardSetCode => {
      try {
        setCards(null);
        const trimmedCardSetCode = cardSetCode.trim();
        if (!trimmedCardSetCode) {
          return;
        }

        setError(null);

        // Make sure we've downloaded and cached the card set
        if (!cardSetCardsCache.has(trimmedCardSetCode)) {
          setGetBoosterStatus('Loading set...');
          await fetchAndCacheCardSet(trimmedCardSetCode);
        }

        setGetBoosterStatus('Picking & packing cards...');
        const newCards = [];
        for (let rarity of getBoosterRarities()) {
          let allCardsOfSameRarity = cardSetCardsCache.get(trimmedCardSetCode)[
            rarity
          ];
          // Some card sets don't have mythics; in those cases, use rares instead
          if (rarity === 'mythic' && allCardsOfSameRarity.length === 0) {
            allCardsOfSameRarity = cardSetCardsCache.get(trimmedCardSetCode)[
              'rare'
            ];
          }
          // Pull a random card of that rarity and add it
          const card =
            allCardsOfSameRarity[
              Math.floor(Math.random() * allCardsOfSameRarity.length)
            ];
          newCards.push(card);
        }
        setCards(newCards);
        setGeneratedCardSetCode(cardSetCode);
      } catch (err) {
        setError(err);
      } finally {
        setGetBoosterStatus('');
      }
    },
    [setCards, setGetBoosterStatus, setError, setGeneratedCardSetCode],
  );

  return {
    generatedCardSetCode,
    cards,
    getBoosterStatus,
    error,
    cacheSize: cardSetCardsCache.size,
    generateBooster,
  };
};

export { useGetBooster };
