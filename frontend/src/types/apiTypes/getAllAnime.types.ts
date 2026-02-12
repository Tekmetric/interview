import { AnimeItem } from '../anime.types';
import { Pagination } from './common';

export type GetAllAnimesResponse = {
  pagination: Pagination,
  data: AnimeItem[];
};
