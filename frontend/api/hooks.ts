import {
  GetNextPageParamFunction,
  useInfiniteQuery,
} from "@tanstack/react-query";
import { fetchTrendingMovies } from "./api";
import { Movie } from "./types";

export const useInfiniteTrendingMovies = (options?: {
  getNextPageParam?: GetNextPageParamFunction<
    number,
    {
      page: number;
      results: Movie[];
      total_pages: number;
      total_results: number;
    }
  >;
}) => {
  return useInfiniteQuery({
    queryKey: ["infinite-trending-movies"],
    queryFn: ({ pageParam }) => fetchTrendingMovies(pageParam),
    initialPageParam: 1,
    getNextPageParam: (_lastPage, _allPages, lastPageParam) => {
      if (lastPageParam === 5) {
        return undefined;
      }

      return lastPageParam + 1;
    },
    ...options,
  });
};
