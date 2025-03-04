import { useQuery } from "@tanstack/react-query";
import { AnimeFilter } from "../utils/constants";

const getTopAnime = async (params) => {
  const { filter = '', page = 1 } = params;

  const response = await fetch(
    `https://api.jikan.moe/v4/top/anime?filter=${filter}&page=${page}`,
  )

  const data = await response.json();
  data.title = AnimeFilter[filter];
  data.filter = filter;
  return data;
};

const getAnimeById = async (id) => {
  const response = await fetch(`https://api.jikan.moe/v4/anime/${id}`);
  const { data } = await response.json();
  return data;
}

const getAnimeCharacters = async (id) => {
  const response = await fetch(`https://api.jikan.moe/v4/anime/${id}/characters`);
  const { data } = await response.json();
  return data.slice(0,10);
}

const getAnimeStaff = async (id) => {
  const response = await fetch(`https://api.jikan.moe/v4/anime/${id}/staff`);
  const { data } = await response.json();
  return data.slice(0,4);
}

const getAnimeReviews = async (id) => {
  const response = await fetch(`https://api.jikan.moe/v4/anime/${id}/reviews?preliminary=true`);
  const { data } = await response.json();
  return data;
}

export const useTopAnimeQuery = (filter = '', page = 1) => {
  return useQuery({
    queryKey: ['topAnime', filter, page],
    queryFn: () => getTopAnime({ filter, page }),
    refetchOnMount: false,
    refetchOnWindowFocus: false,
    refetchOnReconnect: false
  });
};

export const useAnimeCharactersQuery = (id) => {
  return useQuery({
    queryKey: ['animeCharacters', id],
    queryFn: () => getAnimeCharacters(id),
    refetchOnMount: false,
    refetchOnWindowFocus: false,
    refetchOnReconnect: false
  });
}

export const useAnimeStaffQuery = (id) => {
  return useQuery({
    queryKey: ['animeStaff', id],
    queryFn: () => getAnimeStaff(id),
    refetchOnMount: false,
    refetchOnWindowFocus: false,
    refetchOnReconnect: false
  });
}

export const useAnimeReviewsQuery = (id) => {
  return useQuery({
    queryKey: ['animeReviews', id],
    queryFn: () => getAnimeReviews(id),
    refetchOnMount: false,
    refetchOnWindowFocus: false,
    refetchOnReconnect: false
  });
}

export const useAnimeByIdQuery = (id) => {
  return useQuery({
    queryKey: ['animeById', id],
    queryFn: () => getAnimeById(id)
  });
};