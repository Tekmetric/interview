"use client";

import Link from "next/link";
import { moviesMock } from "./movies.mock";
import { PosterCard } from "./poster-card";
import { Section } from "@/components/ui/section";
import { Carousel } from "@/components/ui/carousel";

export const Trending = () => {
  // const movies = await fetchTrendingMovies(1);
  // console.log(movie);

  // console.log(getPosterImageUrl(movie.poster_path, "w92"));

  return (
    <Section className="my-20">
      <Section.Header>Trending Movies</Section.Header>
      <Link href="/trending">
        <Section.Subheader>
          View More
          <Section.SubheaderIcon />
        </Section.Subheader>
      </Link>

      <Carousel className="w-full">
        <Carousel.ScrollLeftButton />
        <Carousel.ScrollRightButton />

        <Carousel.Content>
          {moviesMock.map((movie) => {
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
    </Section>
  );
};
