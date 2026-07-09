export type PageItem = number | 'ellipsis';

export const PRODUCTS_PAGE_SIZE = 12;

export function pageToSkip(page: number): number {
  return (page - 1) * PRODUCTS_PAGE_SIZE;
}

export function getTotalPages(total: number): number {
  return Math.ceil(total / PRODUCTS_PAGE_SIZE);
}

export function getVisiblePages(
  currentPage: number,
  totalPages: number
): PageItem[] {
  if (totalPages <= 1) {
    return [];
  }

  const pages = new Set<number>();

  pages.add(1);
  pages.add(totalPages);

  for (let page = currentPage - 1; page <= currentPage + 1; page++) {
    if (page >= 1 && page <= totalPages) {
      pages.add(page);
    }
  }

  const sorted = [...pages].sort((a, b) => a - b);
  const result: PageItem[] = [];

  for (let index = 0; index < sorted.length; index++) {
    if (index > 0 && sorted[index] - sorted[index - 1] > 1) {
      result.push('ellipsis');
    }
    result.push(sorted[index]);
  }

  return result;
}
