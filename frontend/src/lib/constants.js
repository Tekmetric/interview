// Each item is a separate /objects/{id} request, so this also caps the burst of
// parallel requests per page.
export const RESULTS_PER_PAGE = 24;

// Wait for typing to settle before searching, to avoid a request per keystroke.
export const SEARCH_DEBOUNCE_MS = 750;

// Below this many characters we show the featured landing set instead of searching.
export const MIN_QUERY_LENGTH = 2;

// Broad, image-rich landing query shown before the user searches.
export const DEFAULT_QUERY = 'landscape';

// Kept in English since these are query terms sent to the API (indexed in English).
export const SEARCH_SUGGESTIONS = [
  'Van Gogh',
  'Rembrandt',
  'Vermeer',
  'Samurai',
  'Egypt',
  'Greece',
  'Japan',
  'Cats',
  'Flowers',
  'Gold',
  'Armor',
  'Ceramics',
  'Mythology',
  'Portrait',
];
