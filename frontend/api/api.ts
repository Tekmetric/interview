import { Movie } from "./types";

const IMG_BASE_URL = "https://image.tmdb.org/t/p/";
const API_BASE_URL = "https://api.themoviedb.org/3/";

export type PosterSizes =
  | "w92"
  | "w154"
  | "w185"
  | "w342"
  | "w500"
  | "w780"
  | "original";

const api = (path: string, options?: RequestInit) => {
  const { headers, ...rest } = options ?? {};

  return fetch(
    `${API_BASE_URL}${path}&api_key=${process.env.NEXT_PUBLIC_TMDB_API_KEY}`,
    {
      headers: {
        accept: "application/json",
        ...headers,
      },
      ...rest,
    }
  );
};

export const fetchTrendingMovies = async (
  page: number = 1
): Promise<{
  page: number;
  results: Movie[];
  total_pages: number;
  total_results: number;
}> => {
  const response = await api(`trending/movie/day?language=en-US&page=${page}`);

  if (!response.ok) {
    throw new Error("An error occurred while fetching trending movies");
  }

  return await response.json();
};

export const getPosterImageUrl = (img: string, size: PosterSizes) => {
  return `${IMG_BASE_URL}${size}${img}`;
};
