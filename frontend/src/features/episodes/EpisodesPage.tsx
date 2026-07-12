import { useState } from 'react';
import { FormattedMessage, useIntl } from 'react-intl';
import { Link } from 'react-router';
import styled from 'styled-components';

import { EmptyState, ErrorState } from '../../components/FeedbackState';
import { PortalSpinner } from '../../components/PortalSpinner';
import { ResultsCount } from '../../components/ResultsCount';
import { useGetAllEpisodesQuery } from './api';
import { groupBySeason } from './groupBySeason';

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

const SeasonSection = styled.section`
  border: 1px solid ${({ theme }) => theme.colors.border};
  border-radius: ${({ theme }) => theme.radius.md};
  margin-block-end: ${({ theme }) => theme.space.sm};
  overflow: hidden;
`;

const SeasonHeading = styled.h2`
  font-size: ${({ theme }) => theme.font.size.md};
`;

// The whole season header is the disclosure button (WAI-ARIA accordion
// pattern): aria-expanded carries the state, the panel is labelled by it.
const SeasonButton = styled.button`
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: ${({ theme }) => theme.space.md};
  width: 100%;
  padding: ${({ theme }) => `${theme.space.sm} ${theme.space.md}`};
  border: none;
  background: ${({ theme }) => theme.colors.surface};
  color: ${({ theme }) => theme.colors.text};
  font-weight: ${({ theme }) => theme.font.weight.medium};
  text-align: start;
  cursor: pointer;

  &:hover {
    background: ${({ theme }) => theme.colors.surfaceHover};
  }
`;

const SeasonMeta = styled.span`
  color: ${({ theme }) => theme.colors.textMuted};
  font-size: ${({ theme }) => theme.font.size.sm};
  font-weight: ${({ theme }) => theme.font.weight.regular};
`;

const EpisodeList = styled.ul`
  display: grid;
  gap: ${({ theme }) => theme.space.xs};
  padding: ${({ theme }) => theme.space.md};
  margin: 0;
  list-style: none;
`;

const EpisodeCode = styled.span`
  font-weight: ${({ theme }) => theme.font.weight.medium};
  color: ${({ theme }) => theme.colors.textMuted};
  margin-inline-end: ${({ theme }) => theme.space.sm};
`;

const EpisodeLink = styled(Link)`
  color: ${({ theme }) => theme.colors.text};

  &:hover {
    color: ${({ theme }) => theme.colors.accent};
  }
`;

export function EpisodesPage() {
  const intl = useIntl();
  const { data: episodes, isLoading, isError, refetch } = useGetAllEpisodesQuery();
  const [search, setSearch] = useState('');
  // First season open by default; the user's toggles are kept in a Set.
  const [closedSeasons, setClosedSeasons] = useState<ReadonlySet<number>>(new Set());

  const toggleSeason = (season: number) => {
    setClosedSeasons((previous) => {
      const next = new Set(previous);
      if (next.has(season)) {
        next.delete(season);
      } else {
        next.add(season);
      }
      return next;
    });
  };

  if (isLoading) {
    return <PortalSpinner />;
  }
  if (isError || !episodes) {
    return <ErrorState onRetry={refetch} />;
  }

  // 51 episodes total: filtering in memory is instant and needs no debounce.
  const query = search.trim().toLowerCase();
  const filtered = query
    ? episodes.filter((episode) => episode.name.toLowerCase().includes(query))
    : episodes;
  const seasons = groupBySeason(filtered);

  return (
    <section>
      <h1>
        <FormattedMessage id="episodes.title" />
      </h1>
      <SearchInput
        type="search"
        value={search}
        onChange={(event) => setSearch(event.target.value)}
        placeholder={intl.formatMessage({ id: 'episodes.searchPlaceholder' })}
        aria-label={intl.formatMessage({ id: 'episodes.searchLabel' })}
      />
      <ResultsCount aria-live="polite">
        <FormattedMessage id="episodes.resultsCount" values={{ count: filtered.length }} />
      </ResultsCount>

      {filtered.length === 0 && (
        <EmptyState titleId="episodes.empty.title" descriptionId="episodes.empty.description" />
      )}

      {seasons.map(({ season, episodes: seasonEpisodes }) => {
        // While searching, every season with a match stays open.
        const isOpen = query !== '' || !closedSeasons.has(season);
        const panelId = `season-${season}-panel`;
        return (
          <SeasonSection key={season}>
            <SeasonHeading>
              <SeasonButton
                type="button"
                aria-expanded={isOpen}
                aria-controls={panelId}
                onClick={() => toggleSeason(season)}
              >
                {season === 0 ? (
                  <FormattedMessage id="episodes.specials" />
                ) : (
                  <FormattedMessage id="episodes.season" values={{ season }} />
                )}{' '}
                <SeasonMeta>
                  <FormattedMessage
                    id="episodes.seasonCount"
                    values={{ count: seasonEpisodes.length }}
                  />
                </SeasonMeta>
              </SeasonButton>
            </SeasonHeading>
            {isOpen && (
              <EpisodeList id={panelId}>
                {seasonEpisodes.map((episode) => (
                  <li key={episode.id}>
                    <EpisodeCode>{episode.episode}</EpisodeCode>
                    <EpisodeLink to={`/episodes/${episode.id}`}>{episode.name}</EpisodeLink>
                  </li>
                ))}
              </EpisodeList>
            )}
          </SeasonSection>
        );
      })}
    </section>
  );
}
