"use client";

import Link from "next/link";
import { moviesMock } from "./movies.mock";
import React, { useRef } from "react";
import { cn } from "@/utils/cn";
import { PosterCard } from "./poster-card";

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
    <div
      className={cn(
        "max-w-[1280px] mx-auto",
        "flex flex-col gap-4 items-start",
        "overflow-hidden",
        "p-4 my-10"
      )}
    >
      <h2 className="text-accent text-3xl font-bold">Trending Movies</h2>
      <Link
        href="/trending"
        className="group flex gap-2 items-center leading-none h-6"
      >
        <hr className="border-accent border-0 border-l-4 h-full rounded-full" />
        View More <ChevronRight className="h-4 group-hover:text-accent" />
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
    </div>
  );
};

const ChevronLeft = (props: React.SVGProps<SVGSVGElement>) => {
  return (
    <svg
      width="24"
      height="24"
      xmlns="http://www.w3.org/2000/svg"
      viewBox="0 0 24 24"
      fill="currentColor"
      role="presentation"
      {...props}
    >
      <path d="M18.378 23.369c.398-.402.622-.947.622-1.516 0-.568-.224-1.113-.622-1.515l-8.249-8.34 8.25-8.34a2.16 2.16 0 0 0 .548-2.07A2.132 2.132 0 0 0 17.428.073a2.104 2.104 0 0 0-2.048.555l-9.758 9.866A2.153 2.153 0 0 0 5 12.009c0 .568.224 1.114.622 1.515l9.758 9.866c.808.817 2.17.817 2.998-.021z"></path>
    </svg>
  );
};

const ChevronRight = (props: React.SVGProps<SVGSVGElement>) => {
  return (
    <svg
      width="24"
      height="24"
      xmlns="http://www.w3.org/2000/svg"
      viewBox="0 0 24 24"
      fill="currentColor"
      role="presentation"
      {...props}
    >
      <path d="M5.622.631A2.153 2.153 0 0 0 5 2.147c0 .568.224 1.113.622 1.515l8.249 8.34-8.25 8.34a2.16 2.16 0 0 0-.548 2.07c.196.74.768 1.317 1.499 1.515a2.104 2.104 0 0 0 2.048-.555l9.758-9.866a2.153 2.153 0 0 0 0-3.03L8.62.61C7.812-.207 6.45-.207 5.622.63z"></path>
    </svg>
  );
};
