import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import EventForm from "../components/EventForm";
import { send } from "../utils/send";
import { CircularProgress } from "@mui/material";
import { EventData, EventDataResponse } from "../typings/event_data";

function DetailsPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [eventData, setEventData] = useState<EventData | null>(null);

  useEffect(() => {
    send<EventDataResponse>("GET", `/api/events/${id}`).then(
      (data: EventDataResponse) => {
        setEventData({
          id: data.id,
          title: data.title,
          eventDatetime: data.event_datetime,
          description: data.description,
          eventImageUrl: data.event_image_url,
        });
      }
    );
  }, [id]);

  const handleUpdate = (data: {
    title: string;
    eventDatetime: string;
    description: string;
    eventImageUrl?: string;
  }) => {
    // Update the event data
    send("PUT", `/api/events/${id}/`, {
      title: data.title,
      event_datetime: new Date(data.eventDatetime).toISOString(),
      description: data.description,
      event_image_url: data.eventImageUrl || null,
    }).then(() => navigate("/"));
  };

  if (!eventData) {
    return <CircularProgress />;
  }

  return <EventForm initialData={eventData} onSubmit={handleUpdate} />;
}

export default DetailsPage;
