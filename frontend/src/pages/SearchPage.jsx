import { useState } from 'react';
import { useArtworkBrowse } from '../hooks/useArtworkBrowse';
import { useArtworkModal } from '../context/ArtworkModalContext';
import { getSearchViewState } from '../lib/searchViewState';
import { STATUS } from '../lib/status';
import { SEARCH_SUGGESTIONS } from '../lib/constants';
import SearchBar from '../features/search/SearchBar';
import SearchSuggestions from '../features/search/SearchSuggestions';
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
  const [focused, setFocused] = useState(false);
  const {
    query,
    setQuery,
    submit,
    departmentId,
    setDepartmentId,
    departments,
    isFeatured,
    searchPending,
    initialLoading,
    status,
    total,
    retry,
    items,
    pagedStatus,
    failedCount,
    hasMore,
    loadMore,
  } = useArtworkBrowse();

  const viewState = getSearchViewState({ initialLoading, status, total });

  function handleSearchBlur(e) {
    if (!e.currentTarget.contains(e.relatedTarget)) setFocused(false);
  }
  function pickSuggestion(term) {
    setQuery(term);
    setFocused(false);
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-3 sm:flex-row sm:items-start">
        <div
          className="relative flex-1"
          onFocus={() => setFocused(true)}
          onBlur={handleSearchBlur}
        >
          <SearchBar
            value={query}
            onChange={setQuery}
            onSubmit={submit}
            pending={searchPending}
          />
          {focused && query.trim().length === 0 && (
            <SearchSuggestions
              label="Try searching"
              items={SEARCH_SUGGESTIONS}
              onPick={pickSuggestion}
            />
          )}
        </div>
        <DepartmentFilter
          departments={departments}
          value={departmentId}
          onChange={setDepartmentId}
        />
      </div>

      {viewState === 'loading' && <SearchLoader />}

      {viewState === 'error' && (
        <StatusMessage
          tone="error"
          icon={<IconEmptyFrame className="size-6" />}
          title="The art thief struck again"
          body="We couldn't load the results — looks like someone made off with them. Try again while security's looking."
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

      {viewState === 'results' && (
        <>
          <p className="text-sm text-muted" aria-live="polite">
            {isFeatured ? 'Featured works' : resultsCountLabel(total)}
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
            items.length > 0 && (
              <p className="py-2 text-center text-sm text-muted">
                You've reached the end of the results
              </p>
            )
          )}
        </>
      )}
    </div>
  );
}
