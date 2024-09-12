import { sleep } from '../lib/time';
import { FETCH_DELAY } from './consts';

/**
 * Given a card, returns the price as a float,
 * or null if the price is unknown.
 * @param {{prices: {usd:string|null}}} card
 * @returns {number|float}
 */
const parsePrice = card =>
  card.prices.usd !== null ? Number.parseFloat(card.prices.usd) : null;

// Try to get an image; use large .jpg as fallback image
// if .png doesn't exist, and nothing if neither exists
const parseImgUrl = card =>
  card.image_uris
    ? card.image_uris.png
      ? card.image_uris.png
      : card.image_uris.large
    : '';

/**
 * Fetches all cards for a specific set code;
 * if the fetch is paginated, delays the fetch,
 * then gets the next page (throttling must be done
 * so as not to get blocked by the owner of the API).
 * @param {string} cardSetCode
 * @returns {{ name: string, imgUrl: string, type: string, rarity: 'common'|'uncommon'|'rare'|'mythic', price: number|null}[]}
 */
const fetchCardsFromCardSet = async cardSetCode => {
  const allCardsInSet = {
    common: [],
    uncommon: [],
    rare: [],
    mythic: [],
  };
  let page = 1;
  do {
    const queryParams = new URLSearchParams(
      `q=set:${cardSetCode}&page=${page}`,
    );
    const response = await fetch(
      `https://api.scryfall.com/cards/search?${queryParams}`,
    );
    if (!response.ok) {
      throw new Error('Network failure while fetching cards from Scryfall');
    }
    const result = await response.json();
    for (let card of result.data) {
      // Use facade pattern to select the appropriate data
      // and store it; we don't need EVERYthing the API has to offer
      allCardsInSet[card.rarity].push({
        // Yeah, card.id SHOULD be unique, but it's not. Blame Scryfall.
        id: `${cardSetCode}:${card.name}`,
        name: card.name,
        imgUrl: parseImgUrl(card),
        type: card.type_line,
        rarity: card.rarity,
        price: parsePrice(card),
      });
    }
    if (result.has_more) {
      await sleep(FETCH_DELAY);
      page++;
    } else {
      page = 0;
    }
  } while (page !== 0);
  return allCardsInSet;
};

export { fetchCardsFromCardSet };
