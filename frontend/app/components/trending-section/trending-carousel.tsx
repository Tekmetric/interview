"use client";

import Link from "next/link";
import { PosterCard, PosterCardSkeleton } from "@/components/ui/poster-card";
import { Carousel } from "@/components/ui/carousel";
import { Button } from "@/components/ui/button";
import { useInfiniteTrendingMovies } from "@/api/hooks";
import { cn } from "@/utils/cn";

export const TrendingCarousel = () => {
  const { data, isError, isLoading, refetch } = useInfiniteTrendingMovies();

  if (isLoading) {
    return (
      <Carousel className="w-full h-full" data-testid="skeleton">
        <Carousel.Content className="h-full">
          <PosterCardSkeleton className="w-[185px] h-full shrink-0" />
          <PosterCardSkeleton className="w-[185px] h-full shrink-0" />
          <PosterCardSkeleton className="w-[185px] h-full shrink-0" />
          <PosterCardSkeleton className="w-[185px] h-full shrink-0" />
          <PosterCardSkeleton className="w-[185px] h-full shrink-0" />
          <PosterCardSkeleton className="w-[185px] h-full shrink-0" />
        </Carousel.Content>
      </Carousel>
    );
  }

  if (isError) {
    return (
      <div
        className={cn("flex flex-col gap-2", "w-full items-center", "my-auto")}
      >
        <p>Oops! Something went wrong...</p>
        <Button onClick={() => refetch()}>Try Again</Button>
      </div>
    );
  }

  const movies = data?.pages?.[0].results ?? [];

  return (
    <Carousel className="w-full" data-testid="carousel">
      <Carousel.ScrollLeftButton />
      <Carousel.ScrollRightButton />

      <Carousel.Content>
        {movies.map((movie, index) => {
          return (
            <PosterCard className="w-[185px] flex-shrink-0" key={movie.id}>
              <Link
                href={`movies/${movie.id}`}
                className="hover:opacity-85 transition-opacity duration-200"
              >
                <PosterCard.Image
                  src={movie.poster_path}
                  size="w185"
                  alt={`A poster of ${movie.original_title}`}
                  className="w-full"
                  {...(index <= 5 && { priority: true })} // to improve LCP
                />
              </Link>
              <PosterCard.Content>
                <PosterCard.Rating>
                  {movie.vote_average.toFixed(1)}
                </PosterCard.Rating>
                <Link href={`movies/${movie.id}`} className="hover:underline">
                  <PosterCard.Title title={movie.title}>
                    {movie.title}
                  </PosterCard.Title>
                </Link>
              </PosterCard.Content>
            </PosterCard>
          );
        })}
      </Carousel.Content>
    </Carousel>
  );
};
