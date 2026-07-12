import type { Episode } from '../../api/types';

export interface Season {
  season: number;
  episodes: Episode[];
}

const EPISODE_CODE = /^S(\d+)E\d+$/;

// Groups episodes by the season encoded in their code ("S03E07" => season 3).
// Episodes with an unparseable code land in season 0 ("Specials") so nothing
// silently disappears. Insertion order follows the API's episode order, which
// is already chronological.
export function groupBySeason(episodes: Episode[]): Season[] {
  const bySeason = new Map<number, Episode[]>();

  for (const episode of episodes) {
    const match = EPISODE_CODE.exec(episode.episode);
    const season = match ? Number(match[1]) : 0;
    const group = bySeason.get(season);
    if (group) {
      group.push(episode);
    } else {
      bySeason.set(season, [episode]);
    }
  }

  return [...bySeason.entries()]
    .sort(([a], [b]) => a - b)
    .map(([season, seasonEpisodes]) => ({ season, episodes: seasonEpisodes }));
}
