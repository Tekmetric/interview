import { useInfiniteQuery } from '@tanstack/react-query';
import { fetchArtCrimes } from '@/api/artCrimesApi';
import { FBIArtCrimeResponse } from '@/types/artCrime';
export default function useInifinityFetchArtCrimes(
  filters: Record<string, string | number | undefined>,
) {
  return useInfiniteQuery<FBIArtCrimeResponse, Error>({
    queryKey: ['artCrimes', filters],
    queryFn: async (context) => {
      const pageParam = context.pageParam as number;
      return await fetchArtCrimes({ ...filters, page: pageParam, pageSize: 20 });
    },
    initialPageParam: 1,
    getNextPageParam: (lastPage, _, lastPageParam) => {
      if (lastPage.items.length < 20) {
        return undefined;
      }
      return (lastPageParam as number) + 1;
    },
    staleTime: 1000 * 60 * 5, // 5 minutes
  });
}
