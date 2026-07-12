import { FormattedMessage } from 'react-intl';
import { Link, useParams } from 'react-router';
import styled from 'styled-components';

import { EmptyState, ErrorState } from '../../components/FeedbackState';
import { PortalSpinner } from '../../components/PortalSpinner';
import { StatusBadge } from '../../components/StatusBadge';
import { isNotFoundError } from '../../utils/apiErrors';
import { getIdFromUrl } from '../../utils/getIdFromUrl';
import type { ResourceRef } from '../../api/types';
import { useGetEpisodesByIdsQuery } from '../episodes/api';
import { FavoriteButton } from '../favorites/FavoriteButton';
import { useGetCharacterQuery } from './api';

const Hero = styled.div`
  display: grid;
  gap: ${({ theme }) => theme.space.lg};
  align-items: start;

  @media (min-width: ${({ theme }) => theme.breakpoint.md}) {
    grid-template-columns: 300px 1fr;
  }
`;

const Portrait = styled.img`
  width: 100%;
  max-width: 300px;
  border-radius: ${({ theme }) => theme.radius.lg};
  border: 3px solid ${({ theme }) => theme.colors.portal};
`;

const TitleRow = styled.div`
  display: flex;
  align-items: center;
  gap: ${({ theme }) => theme.space.md};
`;

const Facts = styled.dl`
  display: grid;
  grid-template-columns: max-content 1fr;
  gap: ${({ theme }) => theme.space.xs} ${({ theme }) => theme.space.md};
  margin-top: ${({ theme }) => theme.space.md};

  dt {
    font-weight: ${({ theme }) => theme.font.weight.medium};
    color: ${({ theme }) => theme.colors.textMuted};
  }
`;

const CrossLink = styled(Link)`
  color: ${({ theme }) => theme.colors.accent};
  font-weight: ${({ theme }) => theme.font.weight.medium};
`;

const EpisodeList = styled.ul`
  display: grid;
  gap: ${({ theme }) => theme.space.xs};
  padding: 0;
  margin-top: ${({ theme }) => theme.space.sm};
  list-style: none;
`;

const EpisodeCode = styled.span`
  font-weight: ${({ theme }) => theme.font.weight.medium};
  color: ${({ theme }) => theme.colors.textMuted};
  margin-inline-end: ${({ theme }) => theme.space.sm};
`;

// Origin/location links route internally when the API provides a URL;
// "unknown" places have no URL and render as plain text.
function PlaceValue({ place }: { place: ResourceRef }) {
  const id = place.url ? getIdFromUrl(place.url) : null;
  if (id === null) {
    return <span>{place.name}</span>;
  }
  return <CrossLink to={`/locations/${id}`}>{place.name}</CrossLink>;
}

function EpisodeAppearances({ episodeUrls }: { episodeUrls: string[] }) {
  const ids = episodeUrls.map(getIdFromUrl).filter((id): id is number => id !== null);
  const { data: episodes, isLoading } = useGetEpisodesByIdsQuery(ids, { skip: ids.length === 0 });

  return (
    <section>
      <h2>
        <FormattedMessage id="character.detail.episodes" values={{ count: ids.length }} />
      </h2>
      {isLoading && <PortalSpinner />}
      {episodes && (
        <EpisodeList>
          {episodes.map((episode) => (
            <li key={episode.id}>
              <EpisodeCode>{episode.episode}</EpisodeCode>
              <CrossLink to={`/episodes/${episode.id}`}>{episode.name}</CrossLink>
            </li>
          ))}
        </EpisodeList>
      )}
    </section>
  );
}

export function CharacterDetailPage() {
  const { characterId } = useParams();
  const id = Number(characterId);

  const {
    data: character,
    error,
    isLoading,
    isError,
    refetch,
  } = useGetCharacterQuery(id, {
    skip: !Number.isInteger(id) || id < 1,
  });

  if (isLoading) {
    return <PortalSpinner />;
  }
  if (isError && isNotFoundError(error)) {
    return <EmptyState titleId="notFound.title" descriptionId="character.detail.notFound" />;
  }
  if (isError || !character) {
    return <ErrorState onRetry={refetch} />;
  }

  return (
    <article>
      <Hero>
        {/* The portrait is this page's LCP element: load eagerly, at high priority. */}
        <Portrait src={character.image} alt="" width={300} height={300} fetchPriority="high" />
        <div>
          <TitleRow>
            <h1>{character.name}</h1>
            <FavoriteButton entityType="characters" id={character.id} />
          </TitleRow>
          <StatusBadge status={character.status} />
          <Facts>
            <dt>
              <FormattedMessage id="character.detail.species" />
            </dt>
            <dd>{character.species}</dd>
            {character.type && (
              <>
                <dt>
                  <FormattedMessage id="character.detail.type" />
                </dt>
                <dd>{character.type}</dd>
              </>
            )}
            <dt>
              <FormattedMessage id="character.detail.gender" />
            </dt>
            <dd>
              <FormattedMessage id={`character.gender.${character.gender.toLowerCase()}`} />
            </dd>
            <dt>
              <FormattedMessage id="character.detail.origin" />
            </dt>
            <dd>
              <PlaceValue place={character.origin} />
            </dd>
            <dt>
              <FormattedMessage id="character.detail.lastLocation" />
            </dt>
            <dd>
              <PlaceValue place={character.location} />
            </dd>
          </Facts>
        </div>
      </Hero>
      <EpisodeAppearances episodeUrls={character.episode} />
    </article>
  );
}
