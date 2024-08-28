import { useInfiniteQuery } from "@tanstack/react-query";
import { fetchEvents, PaginatedEventDataResult } from "../api/eventData";

export function usePaginatedEvents(
  filterStartDate: string | undefined,
  filterEndDate: string | undefined
) {
  const {
    data,
    fetchNextPage,
    hasNextPage,
    refetch,
    isLoading,
    isFetching,
    isRefetching,
  } = useInfiniteQuery<PaginatedEventDataResult, Error>({
    queryKey: ["events"],
    queryFn: ({ pageParam }: { pageParam: number | undefined | unknown }) =>
      fetchEvents({ pageParam, filterStartDate, filterEndDate }),
    getNextPageParam: (lastPage) => {
      if (lastPage.next === null) {
        return undefined;
      }
      const nextPageUrl = new URL(lastPage.next);
      return parseInt(nextPageUrl.searchParams.get("page")!);
    },
    enabled: !(
      !!filterStartDate &&
      !!filterEndDate &&
      filterEndDate < filterStartDate
    ),
    staleTime: 1000 * 60 * 5, // 10 minutes
    refetchOnWindowFocus: false,
    initialPageParam: 1,
  });

  const events =
    data?.pages.flatMap((page) =>
      page.results.map((event) => ({
        id: event.id,
        title: event.title,
        eventDatetime: event.event_datetime,
        description: event.description,
        eventImageUrl: event.event_image_url,
      }))
    ) || [];

  return {
    events,
    fetchNextPage,
    hasNextPage,
    refetch,
    isLoading,
    isFetching,
    isRefetching,
  };
}
