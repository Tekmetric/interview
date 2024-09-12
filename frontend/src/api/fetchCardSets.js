// For whatever reason, these sets show up as core sets,
// but they aren't; make sure they don't get included
// in the results.
const NON_CORE_CODES = ['4bb', 'fdn', 'sum', 'fbb'];

const oldestToNewestCardSetSorter = (a, b) =>
  a.released_at < b.released_at ? -1 : 1;

/**
 * Fetches all available card sets,
 * filtering by core and sorting from oldest to newest.
 * @returns {{ name: string, code: string, imgUrl: string, year: number, cardCount: number }[]}
 */
const fetchCardSets = async () => {
  const response = await fetch(`https://api.scryfall.com/sets`);
  if (!response.ok) {
    throw new Error('Network error while fetching card sets from Scryfall');
  }
  const result = await response.json();
  const cardSets = result.data
    .filter(
      cardSet =>
        cardSet.set_type === 'core' && !NON_CORE_CODES.includes(cardSet.code),
    )
    .sort(oldestToNewestCardSetSorter)
    .map(cardSet => ({
      code: cardSet.code,
      name: cardSet.name,
      imgUrl: cardSet.icon_svg_uri,
      year: Number.parseInt(cardSet.released_at.substring(0, 4)),
      cardCount: cardSet.card_count,
    }));
  return cardSets;
};

export { fetchCardSets };
