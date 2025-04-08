import { useQuery } from '@tanstack/react-query';
import { fetchArtCrimeById } from '@/api/artCrimesApi';

export function useArtCrimeById(id: string) {
  return useQuery({
    queryKey: ['artCrime', id],
    queryFn: () => fetchArtCrimeById(id),
    enabled: !!id,
    staleTime: 1000 * 60 * 5, // 5 minutes
  });
}
