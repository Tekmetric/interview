import { FormattedDate, FormattedMessage } from 'react-intl';
import { useParams } from 'react-router';
import styled from 'styled-components';

import { EmptyState, ErrorState } from '../../components/FeedbackState';
import { PortalSpinner } from '../../components/PortalSpinner';
import { isNotFoundError } from '../../utils/apiErrors';
import { getIdFromUrl } from '../../utils/getIdFromUrl';
import { CharacterCard } from '../characters/CharacterCard';
import { useGetCharactersByIdsQuery } from '../characters/api';
import { FavoriteButton } from '../favorites/FavoriteButton';
import { useGetEpisodeQuery } from './api';

const Code = styled.p`
  color: ${({ theme }) => theme.colors.accent};
  font-weight: ${({ theme }) => theme.font.weight.bold};
`;

const TitleRow = styled.div`
  display: flex;
  align-items: center;
  gap: ${({ theme }) => theme.space.md};
`;

const AirDate = styled.p`
  color: ${({ theme }) => theme.colors.textMuted};
  margin-block-end: ${({ theme }) => theme.space.lg};
`;

const Grid = styled.ul`
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: ${({ theme }) => theme.space.md};
  padding: 0;
  list-style: none;
`;

// The API serves air dates as English prose ("December 2, 2013"). When it
// parses, render it localized; otherwise fall back to the raw string.
function LocalizedAirDate({ airDate }: { airDate: string }) {
  const parsed = new Date(airDate);
  if (Number.isNaN(parsed.getTime())) {
    return <>{airDate}</>;
  }
  return <FormattedDate value={parsed} year="numeric" month="long" day="numeric" />;
}

function EpisodeCharacters({ characterUrls }: { characterUrls: string[] }) {
  const ids = characterUrls.map(getIdFromUrl).filter((id): id is number => id !== null);
  const { data: characters, isLoading } = useGetCharactersByIdsQuery(ids, {
    skip: ids.length === 0,
  });

  return (
    <section>
      <h2>
        <FormattedMessage id="episode.detail.characters" values={{ count: ids.length }} />
      </h2>
      {isLoading && <PortalSpinner />}
      {characters && (
        <Grid>
          {characters.map((character) => (
            <li key={character.id}>
              <CharacterCard character={character} />
            </li>
          ))}
        </Grid>
      )}
    </section>
  );
}

export function EpisodeDetailPage() {
  const { episodeId } = useParams();
  const id = Number(episodeId);

  const {
    data: episode,
    error,
    isLoading,
    isError,
    refetch,
  } = useGetEpisodeQuery(id, {
    skip: !Number.isInteger(id) || id < 1,
  });

  if (isLoading) {
    return <PortalSpinner />;
  }
  if (isError && isNotFoundError(error)) {
    return <EmptyState titleId="notFound.title" descriptionId="episode.detail.notFound" />;
  }
  if (isError || !episode) {
    return <ErrorState onRetry={refetch} />;
  }

  return (
    <article>
      <Code>{episode.episode}</Code>
      <TitleRow>
        <h1>{episode.name}</h1>
        <FavoriteButton entityType="episodes" id={episode.id} />
      </TitleRow>
      <AirDate>
        <FormattedMessage id="episode.detail.airDate" />{' '}
        <LocalizedAirDate airDate={episode.air_date} />
      </AirDate>
      <EpisodeCharacters characterUrls={episode.characters} />
    </article>
  );
}
