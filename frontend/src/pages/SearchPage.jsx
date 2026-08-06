import { useArtworkBrowse } from '../hooks/useArtworkBrowse';
import { useArtworkModal } from '../context/ArtworkModalContext';
import { getSearchViewState } from '../lib/searchViewState';
import { STATUS } from '../lib/status';
import SearchBar from '../features/search/SearchBar';
import SearchLanding from '../features/search/SearchLanding';
import DepartmentFilter from '../features/search/DepartmentFilter';
import ResultsList from '../features/results/ResultsList';
import SearchLoader from '../components/SearchLoader';
import StatusMessage from '../components/StatusMessage';
import Button from '../components/Button';
import { IconImage, IconEmptyFrame } from '../components/icons';

function resultsCountLabel(count) {
  return `${count.toLocaleString('en-US')} ${count === 1 ? 'work' : 'works'} found`;
}

function partialFailureLabel(count) {
  return count === 1
    ? "1 work couldn't be loaded and was skipped."
    : `${count} works couldn't be loaded and were skipped.`;
}

export default function SearchPage() {
  const { open } = useArtworkModal();
  const {
    query,
    setQuery,
    submit,
    submitQuery,
    departmentId,
    setDepartmentId,
    departments,
    searchPending,
    status,
    total,
    retry,
    items,
    pagedStatus,
    failedCount,
    hasMore,
    loadMore,
  } = useArtworkBrowse();

  const viewState = getSearchViewState({
    status,
    total,
    pagedStatus,
    itemCount: items.length,
  });

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-3 sm:flex-row sm:items-start">
        <SearchBar
          value={query}
          onChange={setQuery}
          onSubmit={submit}
          pending={searchPending}
        />
        <DepartmentFilter
          departments={departments}
          value={departmentId}
          onChange={setDepartmentId}
        />
      </div>

      {viewState === 'idle' && <SearchLanding onPick={submitQuery} />}

      {viewState === 'loading' && <SearchLoader />}

      {viewState === 'error' && (
        <StatusMessage
          tone="error"
          image={`${import.meta.env.BASE_URL}gallery-closed.png`}
          title="This gallery is briefly closed"
          body="We couldn't load the results just now. Give it a moment and try again."
          onRetry={retry}
          retryLabel="Try again"
        />
      )}

      {viewState === 'empty' && (
        <StatusMessage
          icon={<IconImage className="size-6" />}
          title="No works found"
          body="Try a different search term or department."
        />
      )}

      {viewState === 'page-error' && (
        <StatusMessage
          tone="error"
          icon={<IconEmptyFrame className="size-6" />}
          title="Couldn't load these works"
          body="The collection is rate-limiting us right now. Give it a moment and try again."
          onRetry={retry}
          retryLabel="Try again"
        />
      )}

      {viewState === 'results' && (
        <>
          <p className="text-sm text-muted" aria-live="polite">
            {resultsCountLabel(total)}
          </p>

          <ResultsList items={items} onSelect={open} />

          {failedCount > 0 && (
            <p className="text-center text-xs text-muted" role="status">
              {partialFailureLabel(failedCount)}
            </p>
          )}

          {hasMore ? (
            <div className="flex justify-center">
              <Button
                variant="secondary"
                size="lg"
                onClick={loadMore}
                disabled={pagedStatus === STATUS.loading}
              >
                {pagedStatus === STATUS.loading ? 'Loading…' : 'Load more'}
              </Button>
            </div>
          ) : (
            <p className="py-2 text-center text-sm text-muted">
              You've reached the end of the results
            </p>
          )}
        </>
      )}
    </div>
  );
}
