import React, { useState } from "react";
import EventList from "../components/EventList";
import { Box, Button, Typography } from "@mui/material";
import { useDeleteEventMutation } from "../utils/hooks/eventData";
import styled from "@emotion/styled";
import Filters from "../components/Filters";
import { usePaginatedEvents } from "../utils/hooks/paginatedEvents";

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
  margin: 0 auto;
`;

const ShowMoreContainer = styled(Box)`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
`;

function ListPage() {
  const [filterStartDate, setFilterStartDate] = useState<string | undefined>();
  const [filterEndDate, setFilterEndDate] = useState<string | undefined>();

  const deleteEventMutation = useDeleteEventMutation();

  const { events, fetchNextPage, hasNextPage, refetch } = usePaginatedEvents(
    filterStartDate,
    filterEndDate
  );

  const handleDelete = async (id: number) => {
    deleteEventMutation.mutate(id);
  };

  const handleFilterChange = () => {
    refetch();
  };

  return (
    <PageContainer>
      <Filters
        filterStartDate={filterStartDate}
        filterEndDate={filterEndDate}
        setFilterStartDate={setFilterStartDate}
        setFilterEndDate={setFilterEndDate}
        handleFilterChange={handleFilterChange}
      />
      <EventListContainer>
        <Typography variant="h4">Upcoming Events</Typography>
        <EventList events={events} onDelete={handleDelete} />
        {hasNextPage && (
          <ShowMoreContainer>
            <Button
              variant="contained"
              color="secondary"
              onClick={() => fetchNextPage()}
            >
              <Typography variant="button" style={{ color: "white" }}>
                Show More
              </Typography>
            </Button>
          </ShowMoreContainer>
        )}
      </EventListContainer>
    </PageContainer>
  );
}

export default ListPage;
