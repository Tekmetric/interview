import type { SortOption } from '../types/product';

export const DEFAULT_SORT_OPTION_ID = 'default';

export const SORT_OPTIONS: SortOption[] = [
  { id: DEFAULT_SORT_OPTION_ID, label: 'Default' },
  {
    id: 'price-asc',
    label: 'Price (Low to High)',
    sortBy: 'price',
    order: 'asc',
  },
  {
    id: 'price-desc',
    label: 'Price (High to Low)',
    sortBy: 'price',
    order: 'desc',
  },
  {
    id: 'deal',
    label: 'Best Deal',
    sortBy: 'discountPercentage',
    order: 'desc',
  },
  {
    id: 'rating',
    label: 'Best Rated',
    sortBy: 'rating',
    order: 'desc',
  },
];
