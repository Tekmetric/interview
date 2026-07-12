import { FormattedMessage } from 'react-intl';
import { Link } from 'react-router';
import { skipToken } from '@reduxjs/toolkit/query';
import styled from 'styled-components';

import { useAppSelector } from '../../app/hooks';
import { EmptyState } from '../../components/FeedbackState';
import { PortalSpinner } from '../../components/PortalSpinner';
import { CharacterCard } from '../characters/CharacterCard';
import { useGetCharactersByIdsQuery } from '../characters/api';
import { useGetEpisodesByIdsQuery } from '../episodes/api';
import { useGetLocationsByIdsQuery } from '../locations/api';
import { selectFavoriteIds } from './favoritesSlice';

const Grid = styled.ul`
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: ${({ theme }) => theme.space.md};
  padding: 0;
  list-style: none;
`;

const RowList = styled.ul`
  display: grid;
  gap: ${({ theme }) => theme.space.xs};
  padding: 0;
  list-style: none;
`;

const ItemLink = styled(Link)`
  color: ${({ theme }) => theme.colors.text};

  &:hover {
    color: ${({ theme }) => theme.colors.accent};
  }
`;

const SectionHeading = styled.h2`
  margin-block: ${({ theme }) => theme.space.lg} ${({ theme }) => theme.space.sm};
`;

const ErrorRow = styled.p`
  display: flex;
  align-items: center;
  gap: ${({ theme }) => theme.space.md};
  color: ${({ theme }) => theme.colors.textMuted};
`;

const RetryButton = styled.button`
  padding: ${({ theme }) => `${theme.space.xs} ${theme.space.md}`};
  border: 1px solid ${({ theme }) => theme.colors.border};
  border-radius: ${({ theme }) => theme.radius.pill};
  background: transparent;
  color: ${({ theme }) => theme.colors.text};
  cursor: pointer;

  &:hover {
    background: ${({ theme }) => theme.colors.surfaceHover};
  }
`;

// A failed section must say so — silently dropping it looks like the
// favorites are gone (the API rate-limits bursts, so this does happen).
function SectionError({ titleId, onRetry }: { titleId: string; onRetry: () => void }) {
  return (
    <>
      <SectionHeading>
        <FormattedMessage id={titleId} />
      </SectionHeading>
      <ErrorRow>
        <FormattedMessage id="favorites.section.error" />
        <RetryButton type="button" onClick={onRetry}>
          <FormattedMessage id="common.retry" />
        </RetryButton>
      </ErrorRow>
    </>
  );
}

export function FavoritesPage() {
  const characterIds = useAppSelector((state) => selectFavoriteIds(state, 'characters'));
  const episodeIds = useAppSelector((state) => selectFavoriteIds(state, 'episodes'));
  const locationIds = useAppSelector((state) => selectFavoriteIds(state, 'locations'));

  // skipToken keeps empty sections from firing requests at all.
  const characters = useGetCharactersByIdsQuery(characterIds.length ? characterIds : skipToken);
  const episodes = useGetEpisodesByIdsQuery(episodeIds.length ? episodeIds : skipToken);
  const locations = useGetLocationsByIdsQuery(locationIds.length ? locationIds : skipToken);

  const hasAnyFavorite = characterIds.length + episodeIds.length + locationIds.length > 0;
  const isLoading = characters.isLoading || episodes.isLoading || locations.isLoading;

  return (
    <section>
      <h1>
        <FormattedMessage id="favorites.title" />
      </h1>

      {!hasAnyFavorite && (
        <EmptyState titleId="favorites.empty.title" descriptionId="favorites.empty.description" />
      )}
      {isLoading && <PortalSpinner />}

      {characters.isError && characterIds.length > 0 && (
        <SectionError titleId="nav.characters" onRetry={characters.refetch} />
      )}
      {characters.data && characterIds.length > 0 && (
        <>
          <SectionHeading>
            <FormattedMessage id="nav.characters" />
          </SectionHeading>
          <Grid>
            {characters.data.map((character) => (
              <li key={character.id}>
                <CharacterCard character={character} />
              </li>
            ))}
          </Grid>
        </>
      )}

      {episodes.isError && episodeIds.length > 0 && (
        <SectionError titleId="nav.episodes" onRetry={episodes.refetch} />
      )}
      {episodes.data && episodeIds.length > 0 && (
        <>
          <SectionHeading>
            <FormattedMessage id="nav.episodes" />
          </SectionHeading>
          <RowList>
            {episodes.data.map((episode) => (
              <li key={episode.id}>
                <ItemLink to={`/episodes/${episode.id}`}>
                  {episode.episode} — {episode.name}
                </ItemLink>
              </li>
            ))}
          </RowList>
        </>
      )}

      {locations.isError && locationIds.length > 0 && (
        <SectionError titleId="nav.locations" onRetry={locations.refetch} />
      )}
      {locations.data && locationIds.length > 0 && (
        <>
          <SectionHeading>
            <FormattedMessage id="nav.locations" />
          </SectionHeading>
          <RowList>
            {locations.data.map((location) => (
              <li key={location.id}>
                <ItemLink to={`/locations/${location.id}`}>{location.name}</ItemLink>
              </li>
            ))}
          </RowList>
        </>
      )}
    </section>
  );
}
