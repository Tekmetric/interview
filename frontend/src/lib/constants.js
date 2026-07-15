// Each item is a separate /objects/{id} request, so this also caps the burst of
// parallel requests per page. Kept modest to stay under the API's rate limits.
export const RESULTS_PER_PAGE = 12;

// Wait for typing to settle before searching, to avoid a request per keystroke.
export const SEARCH_DEBOUNCE_MS = 750;

// Typed queries shorter than this aren't committed to the URL (we keep showing
// the landing state); a hand-edited ?q= in the URL is still searched as-is.
export const MIN_QUERY_LENGTH = 2;

// Grouped example searches for the landing state. Terms skew toward
// pre-20th-century, public-domain works so results reliably have open-access
// images, and stay in English since they're query terms sent to the API.
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
