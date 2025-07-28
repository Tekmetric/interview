import Link from "next/link";

import { Section } from "@/components/ui/section";

import { fetchTrendingMovies } from "@/api/api";
import { TrendingCarousel } from "./trending-carousel";

export const Trending = async () => {
  const movies = await fetchTrendingMovies(1);

  return (
    <Section className="my-20">
      <Section.Header>Trending Movies</Section.Header>
      <Link href="/trending">
        <Section.Subheader>
          View More
          <Section.SubheaderIcon />
        </Section.Subheader>
      </Link>

      <TrendingCarousel movies={movies.results} />
    </Section>
  );
};
