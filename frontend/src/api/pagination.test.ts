import { describe, expect, it } from 'vitest';

import { makeCharacter, paginate } from '../test/msw/fixtures';
import { getNextPageNumber } from './pagination';

const characters = Array.from({ length: 25 }, (_, index) => makeCharacter(index + 1));

describe('getNextPageNumber', () => {
  it('requests the following page while the API reports more', () => {
    const page1 = paginate(characters, 1, 'https://example.test/character');
    expect(getNextPageNumber(page1, 1)).toBe(2);
  });

  it('reports no next page once info.next is null', () => {
    const lastPage = paginate(characters, 2, 'https://example.test/character');
    expect(lastPage.info.next).toBeNull();
    expect(getNextPageNumber(lastPage, 2)).toBeUndefined();
  });
});
