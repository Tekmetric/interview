import { useSuspenseQuery } from "@tanstack/react-query";
import { fetchBreweriesAutocomplete } from "@/api/breweries";

export function useBreweriesAutocomplete(query: string, limit: number = 8) {
  return useSuspenseQuery({
    queryKey: ["autocomplete", query, limit] as const,
    queryFn: () => fetchBreweriesAutocomplete(query, limit),
    staleTime: 1000 * 60 * 10,
  });
}
