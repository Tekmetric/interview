"use client";

import { PosterCard, PosterCardSkeleton } from "@/components/ui/poster-card";

import Link from "next/link";
import { cn } from "@/utils/cn";

import { Movie } from "@/api/types";

const Skeleton = ({ className }: { className?: string }) => {
  return (
    <PosterCardSkeleton
      className={cn("w-full pb-[112px]", className)}
      data-testid="skeleton"
    >
      <div className="aspect-[185/278]" />
    </PosterCardSkeleton>
  );
};

export const TrendingGrid = ({
  movies,
  isLoading,
}: {
  movies: Movie[];
  isLoading?: boolean;
}) => {
  return (
    <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-5 w-full gap-x-4 gap-y-8">
      {isLoading && (
        <>
          <Skeleton />
          <Skeleton />
          <Skeleton className="hidden md:block" />
          <Skeleton className="hidden lg:block" />
          <Skeleton className="hidden lg:block" />
        </>
      )}

      {!isLoading &&
        movies.map((movie, index) => {
          return (
            <PosterCard key={movie.id + index} data-testid="poster-card">
              <Link
                href={`movies/${movie.id}`}
                className="hover:opacity-85 transition-opacity duration-200 relative group"
              >
                <PosterCard.Image
                  src={movie.poster_path}
                  size="w342"
                  alt={`A poster of ${movie.original_title}`}
                  className="w-full"
                />
                <div
                  className={cn(
                    "absolute inset-0",
                    "bg-gradient-to-r from-gray-700/80 to-gray-900/80",
                    "opacity-0 group-hover:opacity-100 transition-opacity duration-200",
                    "p-4",
                    "overflow-hidden"
                  )}
                >
                  <p className="line-clamp-[13] lg:line-clamp-[10] text-md">
                    {movie.overview}
                  </p>
                </div>
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
    </div>
  );
};
