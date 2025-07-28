import { Section } from "@/components/ui/section";
import { moviesMock } from "../../components/movies.mock";
import { PosterCard } from "@/components/ui/poster-card";
import Link from "next/link";
import { cn } from "@/utils/cn";

const TrendingPage = () => {
  return (
    <main className="md:max-w-[960px] lg:max-w-[1280px] mx-auto my-20">
      <Section className="gap-10">
        <h1 className="text-5xl">Trending Movies</h1>
        <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-5 w-full gap-x-4 gap-y-8">
          {moviesMock.map((movie) => {
            return (
              <PosterCard className="" key={movie.id}>
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

        <div className="flex w-full justify-center">
          <button className="rounded-full border-2 border-white/70 px-8 cursor-pointer hover:bg-white/10 h-10">
            Load More
          </button>
        </div>
      </Section>
    </main>
  );
};

export default TrendingPage;
