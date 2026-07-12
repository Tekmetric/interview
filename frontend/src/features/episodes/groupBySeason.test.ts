import { describe, expect, it } from 'vitest';

import { makeEpisode } from '../../test/msw/fixtures';
import { groupBySeason } from './groupBySeason';

describe('groupBySeason', () => {
  it('groups episodes by the season in their code, sorted by season', () => {
    const seasons = groupBySeason([
      makeEpisode(4, { episode: 'S02E01' }),
      makeEpisode(1, { episode: 'S01E01' }),
      makeEpisode(2, { episode: 'S01E02' }),
    ]);

    expect(seasons.map((season) => season.season)).toEqual([1, 2]);
    expect(seasons[0].episodes.map((episode) => episode.id)).toEqual([1, 2]);
    expect(seasons[1].episodes.map((episode) => episode.id)).toEqual([4]);
  });

  it('collects episodes with unparseable codes into season 0', () => {
    const seasons = groupBySeason([
      makeEpisode(1, { episode: 'S01E01' }),
      makeEpisode(99, { episode: 'Interdimensional Cable' }),
    ]);

    expect(seasons.map((season) => season.season)).toEqual([0, 1]);
    expect(seasons[0].episodes[0].id).toBe(99);
  });

  it('returns an empty list for no episodes', () => {
    expect(groupBySeason([])).toEqual([]);
  });
});
