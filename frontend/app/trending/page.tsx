"use client";

import { useInfiniteTrendingMovies } from "@/api/hooks";
import { Section } from "@/components/ui/section";
import { TrendingGrid } from "./trending-grid";
import { Button } from "@/components/ui/button";
import { LoaderDots } from "@/components/ui/loader-dots";

const TrendingPage = () => {
  const {
    data,
    isLoading,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
    isLoadingError,
  } = useInfiniteTrendingMovies({
    getNextPageParam: (_lastPage, _allPages, lastPageParam) => {
      // set max loading pages as 5
      if (lastPageParam === 5) {
        return undefined;
      }

      return lastPageParam + 1;
    },
  });

  if (isLoadingError) {
    return <p>Something went wrong 😔</p>;
  }

  const movies = data?.pages.flatMap((page) => page.results) ?? [];

  const showButtonOrLoader = (!isLoading && hasNextPage) || isFetchingNextPage;

  return (
    <main className="md:max-w-[960px] lg:max-w-[1280px] mx-auto my-20">
      <Section className="gap-10">
        <h1 className="text-5xl">Trending Movies</h1>
        <TrendingGrid isLoading={isLoading} movies={movies} />

        {showButtonOrLoader && (
          <div className="flex w-full justify-center">
            {!isLoading && hasNextPage && !isFetchingNextPage && (
              <Button onClick={() => fetchNextPage()}>Load More</Button>
            )}
            {isFetchingNextPage && <LoaderDots />}
          </div>
        )}
      </Section>
    </main>
  );
};

export default TrendingPage;
