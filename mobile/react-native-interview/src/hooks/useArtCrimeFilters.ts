import { useState, useMemo } from 'react';
import { ArtCrimeFilters, ArtCrimeQueryParams, ArtCrimeSorting } from '@/types/artCrime';

export function useArtCrimeFilters() {
  const [filters, setFilters] = useState<ArtCrimeFilters>({});
  const [sort, setSort] = useState<ArtCrimeSorting>({ sort_on: 'modified', sort_order: 'desc' });
  const [debouncedSearch, setDebouncedSearch] = useState('');

  const artCrimesParams: ArtCrimeQueryParams = useMemo(() => {
    return {
      ...(debouncedSearch && { title: debouncedSearch }),
      ...filters,
      ...sort, // TODO: implement sorting, the API supports only 2 sorting options
    };
  }, [debouncedSearch, filters, sort]);

  return {
    filters,
    setFilters,
    sort,
    setSort,
    debouncedSearch,
    setDebouncedSearch,
    artCrimesParams,
  };
}
