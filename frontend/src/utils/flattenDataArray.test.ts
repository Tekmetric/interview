import { flattenDataArray } from './flattenDataArray';
import { AnimeItem } from '../types';

const mockAnime = (id: number, title: string): AnimeItem => ({
  id,
  title,
} as unknown as AnimeItem);

describe('flattenDataArray', () => {
  it('flattens an array of paginated data into a single array', () => {
    const input = [
      {
        data: [mockAnime(1, 'Naruto'), mockAnime(2, 'Bleach')],
        pagination: {},
      },
      {
        data: [mockAnime(3, 'One Piece')],
        pagination: {},
      },
    ];

    const result = flattenDataArray(input);

    expect(result).toHaveLength(3);
    expect(result.map((a) => a.title)).toEqual(['Naruto', 'Bleach', 'One Piece']);
  });

  it('returns an empty array when input is empty', () => {
    const result = flattenDataArray([]);
    expect(result).toEqual([]);
  });

  it('skips items where data is not an array', () => {
    const input = [
      { data: [mockAnime(1, 'Attack on Titan')], pagination: {} },
      { data: null as unknown as AnimeItem[], pagination: {} },
      { data: undefined as unknown as AnimeItem[], pagination: {} },
    ];

    const result = flattenDataArray(input);
    expect(result).toHaveLength(1);
    expect(result[0].title).toBe('Attack on Titan');
  });
});
