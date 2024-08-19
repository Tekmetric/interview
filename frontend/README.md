# Magic: The Gathering Booster Simulator

## Instructions

1. `npm install`
1. `npm start`
1. Visit [http://localhost:3000/](http://localhost:3000/)

## Huh? "Magic?"

Magic is a collectible card game created several decades ago.
You don't need to know how to play, just know that cards
could be purchased in packs of 15, and vary in rarity.

This application simulates opening packs from various sets
over the years, with the latest release at the bottom
and the earliest release at the top. Generating a booster pack
will select cards from that set, with specific rarities chosen
according to how they were distributed:

- 3 Uncommons
- 1 Rare (or a 1:8 chance of a Mythic, if the set allowed)
- 11 Commons

## OK, How Do I Use This?

1. Select a set from the dropdown
1. Click the "Generate Booster" button
1. Examine the cards
1. Click any card to see more details

By generating boosters packs, you can see the total price
of the booster packs, as well as costs of each individual card.

## Application Flow

Initially, the program knows nothing about anything,
so it needs to retrieve the names and codes of the sets.
The bootstrapping process fetches this data from
an API endpoint.

After it's done bootstrapping, the main application will load,
and you have control to expand/collapse the log at the top,
generate boosters, and examine cards in boosters.

## Fetching Details

The two API endpoints provides a list of cards for a given set
and all available sets to choose from. There are more endpoints,
but these were what I needed.

Because the API is public and provides a lot of data,
caution had to be used when crafting a solution.
If the API was hit too often, or too much data was pulled,
an IP ban would be placed, blocking future requests
(this was all mentioned on the API's site).

Retrieval of cards is paginated every 175 cards by the owner
of the API. To ensure a non-blocking experience, a delay was placed
in the app such that paginated calls were not back to back.

Additionally, to limit the retrieval of cards entirely,
after all cards for a set were fetched, I cached them
so I wouldn't need to fetch them again. However, because there are
so many cards across so many sets, there was a chance of using
too much memory in the cache. To combat this, I implemented
a queue for the cache such that the oldest fetched cards
were deleted from the cache when the cache is full.
You can see the cache size in the log at the top of the app
by clicking on the log.

## Limitations

I didn't have time for a lot of things:

- Unit tests
- Mobile-friendly and mobile-first development
- Friendly error handling (the Status component was the best
  I could do on short notice)
- Libraries, etc.
- Extra pretty colors
- Lazy loading (assets are very large over slow networks,
  but it's all the API provided for transparent pictures)
- Lots of other stuff

## Recommendations

- Transition this from JavaScript to Typescript
- Ditch Create React App, as it's no longer supported;
  [Vite](https://vitejs.dev/) is quite en vogue
- Upgrade this to run on a more modern version of Node
