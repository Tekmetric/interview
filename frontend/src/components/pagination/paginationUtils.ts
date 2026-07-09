import { DEFAULT_PAGE_SIZE } from '../../constants/pagination';

export type PageItem = number | 'ellipsis';

export const PRODUCTS_PAGE_SIZE = DEFAULT_PAGE_SIZE;

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
  let previousPage: number | undefined;

  for (const page of sorted) {
    if (previousPage !== undefined && page - previousPage > 1) {
      result.push('ellipsis');
    }
    result.push(page);
    previousPage = page;
  }

  return result;
}
