import { AnimeItem } from '../types';

export const flattenDataArray = (array: { data: AnimeItem[]; pagination: object }[]): any[] => array?.reduce((acc, item) => {
  if (array && Array.isArray(item.data)) {
    acc.push(...item.data);
  }
  return acc || [];
}, [] as any[]);
