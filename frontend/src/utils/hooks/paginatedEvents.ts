import { useInfiniteQuery } from "@tanstack/react-query";
import { request } from "../request";
import { EventDataResponse } from "../../typings/eventData";

type PaginatedEventDataResult = {
  count: number;
  next: string; // URL
  results: EventDataResponse[];
};

async function fetchEvents({
  pageParam = 1,
  filterStartDate,
  filterEndDate,
}: {
  pageParam?: number | undefined | unknown;
  filterStartDate?: string | undefined;
  filterEndDate?: string | undefined;
}): Promise<PaginatedEventDataResult> {
  const params = new URLSearchParams();
  if (pageParam) params.append("page", pageParam.toString());
  if (filterStartDate) params.append("start_date", filterStartDate);
  if (filterEndDate) params.append("end_date", filterEndDate);

  return request<PaginatedEventDataResult>(
    "GET",
    "/api/events/",
    undefined,
    params
  );
}

export function usePaginatedEvents(
  filterStartDate: string | undefined,
  filterEndDate: string | undefined
) {
  const { data, fetchNextPage, hasNextPage, refetch } = useInfiniteQuery<
    PaginatedEventDataResult,
    Error
  >({
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

  return { events, fetchNextPage, hasNextPage, refetch };
}
