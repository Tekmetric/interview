import {
  getTotalPages,
  getVisiblePages,
  pageToSkip,
  PRODUCTS_PAGE_SIZE,
} from '../paginationUtils';

describe('pageToSkip', () => {
  it('maps page 1 to skip 0', () => {
    expect(pageToSkip(1)).toBe(0);
  });

  it('maps page 2 to skip 12', () => {
    expect(pageToSkip(2)).toBe(12);
  });

  it('maps page 5 to skip 48', () => {
    expect(pageToSkip(5)).toBe(48);
  });

  it('uses PRODUCTS_PAGE_SIZE as the stride', () => {
    expect(PRODUCTS_PAGE_SIZE).toBe(12);
    expect(pageToSkip(3)).toBe(24);
  });
});

describe('getTotalPages', () => {
  it('returns 0 for empty totals', () => {
    expect(getTotalPages(0)).toBe(0);
  });

  it('returns 1 for a single item or full page', () => {
    expect(getTotalPages(1)).toBe(1);
    expect(getTotalPages(12)).toBe(1);
  });

  it('returns 2 when one item spills past the first page', () => {
    expect(getTotalPages(13)).toBe(2);
  });

  it('returns 17 for 194 products', () => {
    expect(getTotalPages(194)).toBe(17);
  });
});

describe('getVisiblePages', () => {
  it('returns an empty list when there is only one page', () => {
    expect(getVisiblePages(1, 1)).toEqual([]);
  });

  it('shows all pages for small totals', () => {
    expect(getVisiblePages(1, 3)).toEqual([1, 2, 3]);
    expect(getVisiblePages(2, 3)).toEqual([1, 2, 3]);
  });

  it('shows first pages and last page on page 1 of many', () => {
    expect(getVisiblePages(1, 17)).toEqual([1, 2, 'ellipsis', 17]);
  });

  it('shows a centered window on a middle page', () => {
    expect(getVisiblePages(5, 17)).toEqual([
      1,
      'ellipsis',
      4,
      5,
      6,
      'ellipsis',
      17,
    ]);
    expect(getVisiblePages(9, 17)).toEqual([
      1,
      'ellipsis',
      8,
      9,
      10,
      'ellipsis',
      17,
    ]);
  });

  it('shows last pages on the final page', () => {
    expect(getVisiblePages(17, 17)).toEqual([1, 'ellipsis', 16, 17]);
  });
});
