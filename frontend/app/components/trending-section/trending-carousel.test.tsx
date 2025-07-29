/* eslint-disable @typescript-eslint/no-explicit-any */
import React from "react";
import { render, screen, fireEvent } from "@testing-library/react";
import { TrendingCarousel } from "./trending-carousel";
import { useInfiniteTrendingMovies } from "@/api/hooks";

jest.mock("@/api/hooks");

const mockedUseInfiniteTrendingMovies = jest.mocked(useInfiniteTrendingMovies);

describe("TrendingCarousel", () => {
  afterEach(() => {
    jest.clearAllMocks();
  });

  it("renders skeletons when loading", () => {
    mockedUseInfiniteTrendingMovies.mockReturnValue({
      data: undefined,
      isError: false,
      isLoading: true,
      refetch: jest.fn(),
    } as any);

    render(<TrendingCarousel />);

    expect(screen.getByTestId("skeleton")).toBeInTheDocument();
  });

  it("renders error message when error occurs", () => {
    const refetch = jest.fn();
    mockedUseInfiniteTrendingMovies.mockReturnValue({
      data: undefined,
      isError: true,
      isLoading: false,
      refetch,
    } as any);

    render(<TrendingCarousel />);

    expect(
      screen.getByText("Oops! Something went wrong...")
    ).toBeInTheDocument();

    const button = screen.getByRole("button", { name: /try again/i });
    fireEvent.click(button);

    expect(refetch).toHaveBeenCalled();
  });

  it("renders carousel with movies when data is loaded", () => {
    const movies = [
      {
        id: 1,
        poster_path: "/poster1.jpg",
        original_title: "Movie One",
        title: "Movie One",
        vote_average: 7.5,
      },
      {
        id: 2,
        poster_path: "/poster2.jpg",
        original_title: "Movie Two",
        title: "Movie Two",
        vote_average: 8.2,
      },
    ];
    mockedUseInfiniteTrendingMovies.mockReturnValue({
      data: { pages: [{ results: movies }] },
      isError: false,
      isLoading: false,
      refetch: jest.fn(),
    } as any);

    render(<TrendingCarousel />);

    expect(screen.getByTestId("carousel")).toBeInTheDocument();
    expect(screen.getByText("Movie One")).toBeInTheDocument();
    expect(screen.getByText("Movie Two")).toBeInTheDocument();
  });
});
