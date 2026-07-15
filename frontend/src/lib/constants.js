// Each item is a separate /objects/{id} request, so this also caps the burst of
// parallel requests per page. Kept modest to stay under the API's rate limits.
export const RESULTS_PER_PAGE = 12;

// Wait for typing to settle before searching, to avoid a request per keystroke.
export const SEARCH_DEBOUNCE_MS = 750;

// Below this many characters we show the landing state instead of searching.
export const MIN_QUERY_LENGTH = 2;

// Grouped example searches for the landing state — shows the *kinds* of things
// the collection can be searched by, each with clickable examples that run a
// real search when picked. Kept in English since these are query terms sent to
// the API (indexed in English). Terms are chosen to skew toward pre-20th-century,
// public-domain works so results reliably have open-access images at The Met.
export const SEARCH_CATEGORIES = [
  {
    label: 'Artists',
    hint: 'Search by the maker',
    examples: ['Rembrandt', 'Monet', 'Degas', 'Hokusai'],
  },
  {
    label: 'Regions & cultures',
    hint: 'Where a work comes from',
    examples: ['Egypt', 'Japan', 'Greece', 'Byzantine'],
  },
  {
    label: 'Themes & subjects',
    hint: 'What a work depicts',
    examples: ['Flowers', 'Cats', 'Mythology', 'Portrait'],
  },
  {
    label: 'Styles & movements',
    hint: 'A period or aesthetic',
    examples: ['Impressionism', 'Baroque', 'Renaissance', 'Ukiyo-e'],
  },
  {
    label: 'Mediums & materials',
    hint: 'How a work was made',
    examples: ['Ceramics', 'Armor', 'Textiles', 'Gold'],
  },
];

// Flat quick-pick list for the focus dropdown, derived from the categories so
// there's a single source of truth for example terms.
export const SEARCH_SUGGESTIONS = SEARCH_CATEGORIES.flatMap((c) => c.examples);
