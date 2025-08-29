import { useSuspenseQuery } from "@tanstack/react-query";
import { fetchRandomBreweries } from "@/api/breweries";

export function useRandomBreweries(count: number = 4) {
  return useSuspenseQuery({
    queryKey: ["random-breweries", count] as const,
    queryFn: () => fetchRandomBreweries(count),
    staleTime: 1000 * 60 * 5,
  });
}
