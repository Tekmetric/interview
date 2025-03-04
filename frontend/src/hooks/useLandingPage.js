import { useTopAnimeQuery } from "../services/services";

export const useLandingPage = () => {
  const { 
    isPending: isTopAnimePending,
    error: topAnimeError,
    data: topAnimeData,
    isFetching: isTopAnimeFetching
  } = useTopAnimeQuery('');
  const { 
    isPending: isTopAiringAnimePending,
    error: topAiringAnimeError,
    data: useTopAiringAnimeData,
    isFetching: isTopAiringAnimeFetching
  } = useTopAnimeQuery('airing');
  const { 
    isPending: isTopUpcomingAnimePenging,
    error: topUpcomingAnimeError,
    data: useTopUpcomingAnimeData,
    isFetching: isTopUpcomingAnimeFetching
  } = useTopAnimeQuery('upcoming');

  const isPending = isTopAnimePending || isTopAiringAnimePending || isTopUpcomingAnimePenging;
  const isFetching = isTopAnimeFetching || isTopAiringAnimeFetching || isTopUpcomingAnimeFetching;
  const error = topAnimeError || topAiringAnimeError || topUpcomingAnimeError;
  const data = [topAnimeData, useTopAiringAnimeData, useTopUpcomingAnimeData];

  return {
    isLoading: isPending || isFetching,
    error,
    data,
  }
}