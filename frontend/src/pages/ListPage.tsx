import React, { useState } from "react";
import EventList from "../components/EventList";
import { Box, Button, TextField } from "@mui/material";
import { EventDataResponse } from "../typings/eventData";
import { useDeleteEventMutation } from "../utils/hooks/eventData";
import { useInfiniteQuery } from "@tanstack/react-query";
import { send } from "../utils/send";

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

  return send<PaginatedEventDataResult>(
    "GET",
    "/api/events/",
    undefined,
    params
  );
}

function ListPage() {
  const [filterStartDate, setFilterStartDate] = useState<string | undefined>();
  const [filterEndDate, setFilterEndDate] = useState<string | undefined>();

  const deleteEventMutation = useDeleteEventMutation();

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

  const handleDelete = async (id: number) => {
    deleteEventMutation.mutate(id);
  };

  const handleFilterChange = () => {
    refetch();
  };

  return (
    <Box>
      <TextField
        id="filterStartDate"
        name="filterStartDate"
        type="datetime-local"
        label="Filter Start Date"
        InputLabelProps={{ shrink: true }}
        value={filterStartDate}
        onChange={(e) => setFilterStartDate(e.target.value)}
        required
      />
      <TextField
        id="filterEndDate"
        name="filterEndDate"
        type="datetime-local"
        label="Filter End Date"
        InputLabelProps={{ shrink: true }}
        value={filterEndDate}
        onChange={(e) => setFilterEndDate(e.target.value)}
        required
      />
      <Button variant="contained" onClick={handleFilterChange}>
        Apply Filters
      </Button>
      <EventList events={events} onDelete={handleDelete} />
      {hasNextPage && (
        <Button variant="contained" onClick={() => fetchNextPage()}>
          Show More
        </Button>
      )}
    </Box>
  );
}

export default ListPage;
