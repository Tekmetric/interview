import React, { useState } from "react";
import EventList from "../components/EventList";
import { Box, Button, CircularProgress, Typography } from "@mui/material";
import { useDeleteEventMutation } from "../utils/hooks/eventData";
import styled from "@emotion/styled";
import Filters from "../components/Filters";
import { usePaginatedEvents } from "../utils/hooks/paginatedEvents";
import posthog from "posthog-js";
import { EventData } from "../typings/eventData";

const EventListContainer = styled(Box)`
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 700px;
  padding-top: 24px;
  @media (max-width: 400px) {
    width: 400px;
  }
`;

const PageContainer = styled(Box)`
  display: flex;
  flex-direction: column;
  align-items: center;
  margin: 16px auto 32px;
`;

const ShowMoreContainer = styled(Box)`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
`;

function EventListSection({
  events,
  isLoading,
  isFetching,
  isRefetching,
  fetchNextPage,
  hasNextPage,
}: {
  events: EventData[];
  isLoading: boolean;
  isFetching: boolean;
  isRefetching: boolean;
  fetchNextPage: () => void;
  hasNextPage: boolean;
}) {
  const deleteEventMutation = useDeleteEventMutation();

  const handleDelete = async (id: number) => {
    deleteEventMutation.mutate(id);
  };

  if (isRefetching) {
    return <CircularProgress sx={{ marginTop: "16px" }} />;
  }

  return (
    <EventListContainer>
      <Typography variant="h4">Upcoming Events</Typography>
      {!isLoading && <EventList events={events} onDelete={handleDelete} />}
      {isFetching && <CircularProgress />}
      {hasNextPage && !isFetching && (
        <ShowMoreContainer>
          <Button
            variant="contained"
            color="secondary"
            onClick={() => {
              posthog.capture("ShowMoreButtonClicked");
              fetchNextPage();
            }}
          >
            <Typography variant="button" style={{ color: "white" }}>
              Show More
            </Typography>
          </Button>
        </ShowMoreContainer>
      )}
    </EventListContainer>
  );
}

function ListPage() {
  const [filterStartDate, setFilterStartDate] = useState<string | undefined>();
  const [filterEndDate, setFilterEndDate] = useState<string | undefined>();

  const errorBetweenFilterDates =
    !!filterStartDate && !!filterEndDate && filterStartDate > filterEndDate;

  const {
    events,
    fetchNextPage,
    hasNextPage,
    refetch,
    isFetching,
    isLoading,
    isRefetching,
  } = usePaginatedEvents(filterStartDate, filterEndDate);

  const handleFilterChange = () => {
    refetch();
  };

  return (
    <PageContainer>
      {errorBetweenFilterDates && (
        <Typography variant="body1" color="error">
          Start date must be before end date
        </Typography>
      )}
      <Filters
        filterStartDate={filterStartDate}
        filterEndDate={filterEndDate}
        setFilterStartDate={setFilterStartDate}
        setFilterEndDate={setFilterEndDate}
        handleFilterChange={handleFilterChange}
      />
      <EventListSection
        events={events}
        isLoading={isLoading}
        isFetching={isFetching}
        isRefetching={isRefetching}
        hasNextPage={hasNextPage}
        fetchNextPage={fetchNextPage}
      />
    </PageContainer>
  );
}

export default ListPage;
