import { FormattedMessage } from 'react-intl';
import { useSearchParams } from 'react-router';

import { EmptyState, ErrorState } from '../../components/FeedbackState';
import { LoadMoreButton } from '../../components/LoadMoreButton';
import { PortalSpinner } from '../../components/PortalSpinner';
import { ResultsCount } from '../../components/ResultsCount';
import { isNotFoundError } from '../../utils/apiErrors';
import { characterFiltersFromSearchParams, CharacterFilters } from './CharacterFilters';
import { VirtualizedCharacterGrid } from './VirtualizedCharacterGrid';
import { useGetCharactersInfiniteQuery } from './api';

export function CharactersPage() {
  const [searchParams] = useSearchParams();
  const filters = characterFiltersFromSearchParams(searchParams);

  const {
    data,
    error,
    isLoading,
    isError,
    isSuccess,
    hasNextPage,
    isFetchingNextPage,
    fetchNextPage,
    refetch,
  } = useGetCharactersInfiniteQuery(filters);

  const characters = data?.pages.flatMap((page) => page.results) ?? [];
  const totalCount = data?.pages[0]?.info.count ?? 0;

  // The API reports "no matches" as a 404 — an empty state, not a failure.
  const isEmptyResult = isError && isNotFoundError(error);

  return (
    <section>
      <h1>
        <FormattedMessage id="characters.title" />
      </h1>
      <CharacterFilters />
      <ResultsCount aria-live="polite">
        {isSuccess && (
          <FormattedMessage id="characters.resultsCount" values={{ count: totalCount }} />
        )}
        {isEmptyResult && <FormattedMessage id="characters.resultsCount" values={{ count: 0 }} />}
      </ResultsCount>

      {isLoading && <PortalSpinner />}
      {isEmptyResult && (
        <EmptyState titleId="characters.empty.title" descriptionId="characters.empty.description" />
      )}
      {isError && !isEmptyResult && <ErrorState onRetry={refetch} />}

      {isSuccess && (
        <>
          <VirtualizedCharacterGrid characters={characters} />
          {hasNextPage && (
            <LoadMoreButton
              labelId="characters.loadMore"
              loading={isFetchingNextPage}
              onClick={() => fetchNextPage()}
            />
          )}
        </>
      )}
    </section>
  );
}
