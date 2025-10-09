import { AnimeItem } from '../types';

export const getRowsFromData = (data: AnimeItem[]) => data?.map((item) => ({
  title: item.title,
  genres: item.genres,
  type: item.type,
  episodes: item.episodes,
  airedFrom: item.aired.from,
  airedTo: item.aired.to,
  rating: item.rating,
  score: item.score,
  id: item.mal_id,
}));
