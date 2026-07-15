import { STATUS } from './status';

// Collapses the search + paging status into the single thing the page renders,
// so SearchPage can switch on one value instead of a nested ladder.
export function getSearchViewState({ status, total, pagedStatus, itemCount }) {
  if (status === STATUS.error) return 'error';
  if (status !== STATUS.success) return status === STATUS.loading ? 'loading' : 'idle';

  if (total === 0) return 'empty';
  if (itemCount > 0) return 'results';
  // Search matched, but this page has no items yet: either still fetching the
  // details, or every object in the page failed to load.
  return pagedStatus === STATUS.success ? 'page-error' : 'loading';
}
