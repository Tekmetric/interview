import { useAnimeByIdQuery, useAnimeCharactersQuery, useAnimeReviewsQuery, useAnimeStaffQuery } from "../services/services";

export const useIndividualAnime = (id) => {
  const { 
    data,
    error: dataError,
    isPending: isDataPending,
    isFetching: isDataFetching
  } = useAnimeByIdQuery(id);
  const { 
    data: characters,
    error: charactersError,
    isPending: isCharactersPending,
    isFetching: isCharactersFetching
  } = useAnimeCharactersQuery(id);
  const { 
    data: staff,
    error: staffError,
    isPending: isStaffPending,
    isFetching: isStaffFetching
  } = useAnimeStaffQuery(id);
  const { 
    data: reviews,
    error: reviewsError,
    isPending: isReviewsPending,
    isFetching: isReviewsFetching
  } = useAnimeReviewsQuery(id);

  const isPending = isDataPending || isCharactersPending || isStaffPending || isReviewsPending;
  const isFetching = isDataFetching || isCharactersFetching || isStaffFetching || isReviewsFetching;
  const error = data && !Object.keys(data).length && (dataError || charactersError || staffError || reviewsError);

  return {
    isLoading: isPending || isFetching,
    error,
    data,
    characters,
    staff,
    reviews
  }
}