import { useQuery } from '@tanstack/react-query';
import { fetchArtCrimes } from '../api/artCrimesApi';

export default function useFetchArtCrimes(filters: Record<string, string | number | undefined>) {
  return useQuery({
    queryKey: ['artCrimes', filters],
    queryFn: () => fetchArtCrimes(filters),
  });
}
