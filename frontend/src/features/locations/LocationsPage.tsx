import { useEffect, useState } from 'react';
import { FormattedMessage, useIntl } from 'react-intl';
import { Link, useSearchParams } from 'react-router';
import styled from 'styled-components';

import { EmptyState, ErrorState } from '../../components/FeedbackState';
import { LoadMoreButton } from '../../components/LoadMoreButton';
import { PortalSpinner } from '../../components/PortalSpinner';
import { ResultsCount } from '../../components/ResultsCount';
import { useDebouncedValue } from '../../hooks/useDebouncedValue';
import { isNotFoundError } from '../../utils/apiErrors';
import { useGetLocationsInfiniteQuery } from './api';

const SearchInput = styled.input`
  width: min(100%, 24rem);
  margin-block: ${({ theme }) => theme.space.md};
  padding: ${({ theme }) => `${theme.space.sm} ${theme.space.md}`};
  border: 1px solid ${({ theme }) => theme.colors.border};
  border-radius: ${({ theme }) => theme.radius.pill};
  background: ${({ theme }) => theme.colors.background};
  color: ${({ theme }) => theme.colors.text};

  &::placeholder {
    color: ${({ theme }) => theme.colors.textMuted};
  }
`;

const List = styled.ul`
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: ${({ theme }) => theme.space.md};
  padding: 0;
  list-style: none;
`;

const Card = styled.article`
  position: relative;
  height: 100%;
  padding: ${({ theme }) => theme.space.md};
  border: 1px solid ${({ theme }) => theme.colors.border};
  border-radius: ${({ theme }) => theme.radius.lg};
  background: ${({ theme }) => theme.colors.surface};
  transition: border-color ${({ theme }) => theme.transition.fast};

  &:hover,
  &:focus-within {
    border-color: ${({ theme }) => theme.colors.accent};
  }
`;

const Name = styled.h2`
  font-size: ${({ theme }) => theme.font.size.md};
`;

const NameLink = styled(Link)`
  color: ${({ theme }) => theme.colors.text};
  text-decoration: none;

  &::after {
    content: '';
    position: absolute;
    inset: 0;
  }
`;

const Meta = styled.p`
  color: ${({ theme }) => theme.colors.textMuted};
  font-size: ${({ theme }) => theme.font.size.sm};
`;

export function LocationsPage() {
  const intl = useIntl();
  const [searchParams, setSearchParams] = useSearchParams();
  const urlName = searchParams.get('name') ?? '';

  const [nameInput, setNameInput] = useState(urlName);
  const debouncedName = useDebouncedValue(nameInput, 300);

  useEffect(() => {
    setSearchParams(
      (previous) => {
        const next = new URLSearchParams(previous);
        if (debouncedName) {
          next.set('name', debouncedName);
        } else {
          next.delete('name');
        }
        return next;
      },
      { replace: true },
    );
  }, [debouncedName, setSearchParams]);

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
  } = useGetLocationsInfiniteQuery(debouncedName ? { name: debouncedName } : {});

  const locations = data?.pages.flatMap((page) => page.results) ?? [];
  const totalCount = data?.pages[0]?.info.count ?? 0;
  const isEmptyResult = isError && isNotFoundError(error);

  return (
    <section>
      <h1>
        <FormattedMessage id="locations.title" />
      </h1>
      <SearchInput
        type="search"
        value={nameInput}
        onChange={(event) => setNameInput(event.target.value)}
        placeholder={intl.formatMessage({ id: 'locations.searchPlaceholder' })}
        aria-label={intl.formatMessage({ id: 'locations.searchLabel' })}
      />
      <ResultsCount aria-live="polite">
        {isSuccess && (
          <FormattedMessage id="locations.resultsCount" values={{ count: totalCount }} />
        )}
        {isEmptyResult && <FormattedMessage id="locations.resultsCount" values={{ count: 0 }} />}
      </ResultsCount>

      {isLoading && <PortalSpinner />}
      {isEmptyResult && (
        <EmptyState titleId="locations.empty.title" descriptionId="locations.empty.description" />
      )}
      {isError && !isEmptyResult && <ErrorState onRetry={refetch} />}

      {isSuccess && (
        <>
          <List>
            {locations.map((location) => (
              <li key={location.id}>
                <Card>
                  <Name>
                    <NameLink to={`/locations/${location.id}`}>{location.name}</NameLink>
                  </Name>
                  <Meta>{location.type}</Meta>
                  <Meta>{location.dimension}</Meta>
                </Card>
              </li>
            ))}
          </List>
          {hasNextPage && (
            <LoadMoreButton
              labelId="locations.loadMore"
              loading={isFetchingNextPage}
              onClick={() => fetchNextPage()}
            />
          )}
        </>
      )}
    </section>
  );
}
