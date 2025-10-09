import { getRowsFromData } from './getRowsFromData';
import { AnimeItem } from '../types';

import { animeItemMock, animeRowMock } from '../mocks';

describe('getRowsFromData', () => {
  it('should transform AnimeItem data into rows', () => {
    const data: AnimeItem[] = [animeItemMock];

    const expected = [animeRowMock];

    expect(getRowsFromData(data)).toEqual(expected);
  });

  it('should return an empty array when data is empty', () => {
    const data: AnimeItem[] = [];
    expect(getRowsFromData(data)).toEqual([]);
  });
});
