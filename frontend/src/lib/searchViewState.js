import { STATUS } from './status';

// Collapses the search status + result count into the single thing the page
// renders, so SearchPage can switch on one value instead of a boolean ladder.
export function getSearchViewState({ initialLoading, status, total }) {
  if (initialLoading) return 'loading';
  if (status === STATUS.error) return 'error';
  if (status === STATUS.success) return total > 0 ? 'results' : 'empty';
  return 'idle';
}
