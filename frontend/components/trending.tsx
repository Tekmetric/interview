"use client";

import Link from "next/link";
import { moviesMock } from "./movies.mock";
import React, { useRef } from "react";
import { cn } from "@/utils/cn";
import { PosterCard } from "./poster-card";
import { Section } from "@/components/ui/section";
import { ChevronLeft, ChevronRight } from "@/components/ui/icons";

export const Trending = () => {
  // const movies = await fetchTrendingMovies(1);
  // console.log(movie);

  // console.log(getPosterImageUrl(movie.poster_path, "w92"));

  const containerRef = useRef<HTMLDivElement>(null);

  const onScrollLeft = () => {
    const container = containerRef.current;
    if (!container) {
      return;
    }

    const visibleWidth = container.getBoundingClientRect().width;
    container?.scrollTo({
      left: Math.max(0, container.scrollLeft - visibleWidth * 0.9),
      behavior: "smooth",
    });
  };

  const onScrollRight = () => {
    const container = containerRef.current;
    if (!container) {
      return;
    }

    const visibleWidth = container.getBoundingClientRect().width;
    container?.scrollTo({
      left: container.scrollLeft + visibleWidth * 0.9,
      behavior: "smooth",
    });
  };

  return (
    <Section className="my-20">
      <Section.Header>Trending Movies</Section.Header>
      <Link href="/trending">
        <Section.Subheader>
          View More
          <Section.SubheaderIcon />
        </Section.Subheader>
      </Link>

      <div
        data-testid="carousel-wrapper"
        className="relative isolate group w-full"
      >
        <button
          className={cn(
            "z-10 absolute left-0 top-1/2 -translate-y-1/2 -translate-x-1/4",
            "hidden lg:flex items-center justify-center",
            "opacity-0 group-hover:opacity-100 focus-visible:opacity-100",
            "bg-black/40",
            "w-10 h-20",
            "cursor-pointer",
            "hover:text-accent focus-visible:text-accent",
            "border-1 border-white/70 rounded-lg outline-0"
          )}
          onClick={onScrollLeft}
        >
          <ChevronLeft />
        </button>
        <button
          className={cn(
            "z-10 absolute right-0 top-1/2 -translate-y-1/2 translate-x-1/4",
            "hidden lg:flex items-center justify-center",
            "opacity-0 group-hover:opacity-100 focus-visible:opacity-100",
            "bg-black/40",
            "w-10 h-20",
            "cursor-pointer",
            "hover:text-accent focus-visible:text-accent",
            "border-1 border-white/70 rounded-lg outline-0"
          )}
          onClick={onScrollRight}
        >
          <ChevronRight />
        </button>
        <div
          className="flex space-x-4 overflow-auto no-scrollbar"
          ref={containerRef}
        >
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
        </div>
      </div>
    </Section>
  );
};
