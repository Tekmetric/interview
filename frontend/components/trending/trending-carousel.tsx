"use client";

import Link from "next/link";
import { PosterCard } from "@/components/ui/poster-card";
import { Carousel } from "@/components/ui/carousel";
import { Movie } from "@/api/types";

export const TrendingCarousel = ({ movies }: { movies: Movie[] }) => {
  return (
    <Carousel className="w-full">
      <Carousel.ScrollLeftButton />
      <Carousel.ScrollRightButton />

      <Carousel.Content>
        {movies.map((movie) => {
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
