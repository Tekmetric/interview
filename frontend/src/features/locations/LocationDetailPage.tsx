import { FormattedMessage } from 'react-intl';
import { useParams } from 'react-router';
import styled from 'styled-components';

import { EmptyState, ErrorState } from '../../components/FeedbackState';
import { PortalSpinner } from '../../components/PortalSpinner';
import { isNotFoundError } from '../../utils/apiErrors';
import { getIdFromUrl } from '../../utils/getIdFromUrl';
import { CharacterCard } from '../characters/CharacterCard';
import { useGetCharactersByIdsQuery } from '../characters/api';
import { FavoriteButton } from '../favorites/FavoriteButton';
import { useGetLocationQuery } from './api';

const TitleRow = styled.div`
  display: flex;
  align-items: center;
  gap: ${({ theme }) => theme.space.md};
`;

const Facts = styled.dl`
  display: grid;
  grid-template-columns: max-content 1fr;
  gap: ${({ theme }) => theme.space.xs} ${({ theme }) => theme.space.md};
  margin-block: ${({ theme }) => theme.space.md} ${({ theme }) => theme.space.lg};

  dt {
    font-weight: ${({ theme }) => theme.font.weight.medium};
    color: ${({ theme }) => theme.colors.textMuted};
  }
`;

const Grid = styled.ul`
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: ${({ theme }) => theme.space.md};
  padding: 0;
  list-style: none;
`;

function Residents({ residentUrls }: { residentUrls: string[] }) {
  const ids = residentUrls.map(getIdFromUrl).filter((id): id is number => id !== null);
  const { data: residents, isLoading } = useGetCharactersByIdsQuery(ids, {
    skip: ids.length === 0,
  });

  return (
    <section>
      <h2>
        <FormattedMessage id="location.detail.residents" values={{ count: ids.length }} />
      </h2>
      {ids.length === 0 && (
        <p>
          <FormattedMessage id="location.detail.noResidents" />
        </p>
      )}
      {isLoading && <PortalSpinner />}
      {residents && (
        <Grid>
          {residents.map((character) => (
            <li key={character.id}>
              <CharacterCard character={character} />
            </li>
          ))}
        </Grid>
      )}
    </section>
  );
}

export function LocationDetailPage() {
  const { locationId } = useParams();
  const id = Number(locationId);

  const {
    data: location,
    error,
    isLoading,
    isError,
    refetch,
  } = useGetLocationQuery(id, {
    skip: !Number.isInteger(id) || id < 1,
  });

  if (isLoading) {
    return <PortalSpinner />;
  }
  if (isError && isNotFoundError(error)) {
    return <EmptyState titleId="notFound.title" descriptionId="location.detail.notFound" />;
  }
  if (isError || !location) {
    return <ErrorState onRetry={refetch} />;
  }

  return (
    <article>
      <TitleRow>
        <h1>{location.name}</h1>
        <FavoriteButton entityType="locations" id={location.id} />
      </TitleRow>
      <Facts>
        <dt>
          <FormattedMessage id="location.detail.type" />
        </dt>
        <dd>{location.type}</dd>
        <dt>
          <FormattedMessage id="location.detail.dimension" />
        </dt>
        <dd>{location.dimension}</dd>
      </Facts>
      <Residents residentUrls={location.residents} />
    </article>
  );
}
