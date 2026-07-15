import { useState } from 'react';
import { useTranslation } from '../i18n/LocaleProvider';
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

export default function SearchPage() {
  const { t } = useTranslation();
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
              label={t('search.suggestionsLabel')}
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
          title={t('search.errorTitle')}
          body={t('search.errorBody')}
          onRetry={retry}
          retryLabel={t('search.retry')}
        />
      )}

      {viewState === 'empty' && (
        <StatusMessage
          icon={<IconImage className="size-6" />}
          title={t('search.noResultsTitle')}
          body={t('search.noResultsBody')}
        />
      )}

      {viewState === 'results' && (
        <>
          <p className="text-sm text-muted" aria-live="polite">
            {isFeatured
              ? t('search.featuredTitle')
              : t('search.resultsCount', { count: total })}
          </p>

          <ResultsList items={items} onSelect={open} />

          {failedCount > 0 && (
            <p className="text-center text-xs text-muted" role="status">
              {t('search.partialFailure', { count: failedCount })}
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
                {pagedStatus === STATUS.loading
                  ? t('search.loadingMore')
                  : t('search.loadMore')}
              </Button>
            </div>
          ) : (
            items.length > 0 && (
              <p className="py-2 text-center text-sm text-muted">
                {t('search.endOfResults')}
              </p>
            )
          )}
        </>
      )}
    </div>
  );
}
