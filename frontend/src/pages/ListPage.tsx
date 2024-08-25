import React, { useEffect, useState } from "react";
import EventList from "../components/EventList";
import { Box } from "@mui/material";
import { send } from "../utils/send";
import { EventData, EventDataResponse } from "../typings/eventData";
import { useDeleteEventMutation } from "../utils/hooks.ts/eventData";

type PaginatedEventDataResult = {
  count: number;
  results: EventDataResponse[];
};

function ListPage() {
  const [events, setEvents] = useState<EventData[]>([]);
  const deleteEventMutatio = useDeleteEventMutation();

  useEffect(() => {
    send<PaginatedEventDataResult>("GET", "/api/events/").then(
      (data: PaginatedEventDataResult) => {
        setEvents(
          data.results.map((event) => ({
            id: event.id,
            title: event.title,
            eventDatetime: event.event_datetime,
            description: event.description,
            eventImageUrl: event.event_image_url,
          }))
        );
      }
    );
  }, []);

  const handleDelete = async (id: number) => {
    deleteEventMutatio.mutate(id);
    setEvents((prevEvents) => prevEvents.filter((event) => event.id !== id));
  };

  return (
    <Box>
      <EventList events={events} onDelete={handleDelete} />
    </Box>
  );
}

export default ListPage;
