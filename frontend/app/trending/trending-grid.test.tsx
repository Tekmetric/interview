import React from "react";
import { render, screen } from "@testing-library/react";
import { TrendingGrid } from "./trending-grid";
import { Movie } from "@/api/types";

const movies = [
  {
    id: 1,
    poster_path: "/poster1.jpg",
    original_title: "Movie 1",
    overview: "Overview 1",
    vote_average: 8.1,
    title: "Movie 1",
  },
  {
    id: 2,
    poster_path: "/poster2.jpg",
    original_title: "Movie 2",
    overview: "Overview 2",
    vote_average: 7.5,
    title: "Movie 2",
  },
] as Movie[];

describe("TrendingGrid", () => {
  it("renders skeletons on loading and does not render poster cards", () => {
    render(<TrendingGrid movies={movies} isLoading={true} />);

    expect(screen.getAllByTestId("skeleton")[0]).toBeInTheDocument();
    expect(screen.queryByTestId("poster-card")).toBeNull();
  });

  it("renders poster cards when loaded and does not render skeletons", () => {
    render(<TrendingGrid movies={movies} isLoading={false} />);

    expect(screen.getAllByTestId("poster-card").length).toBe(movies.length);
    expect(screen.queryByTestId("skeleton")).toBeNull();
  });
});
