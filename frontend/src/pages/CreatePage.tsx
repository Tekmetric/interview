import React from "react";
import { useNavigate } from "react-router-dom";
import EventForm from "../components/EventForm";
import { send } from "../utils/send";

function CreatePage() {
  const navigate = useNavigate();

  const handleCreate = (data: {
    title: string;
    eventDatetime: string;
    description: string;
    evetImageUrl?: string;
  }) => {
    // Save the event data
    send("POST", "/api/events/", {
      title: data.title,
      event_datetime: new Date(data.eventDatetime).toISOString(),
      description: data.description,
      event_image_url: data.evetImageUrl || null,
    }).then(() => navigate("/"));
  };

  return <EventForm onSubmit={handleCreate} />;
}

export default CreatePage;
