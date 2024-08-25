import React, { useState } from "react";
import { Box, TextField, Button } from "@mui/material";
import { EventData } from "../typings/event_data";

interface EventFormProps {
  initialData?: EventData;
  onSubmit: (data: {
    title: string;
    eventDatetime: string;
    description: string;
    eventImageUrl?: string;
  }) => void;
}

// const formStyles = css`
//   display: flex,
//   flexDirection: column,
//   gap: 16px,
//   maxWidth: 400px,
//   margin: 0 auto,
// `;

function convertToLocaleDatetime(isoDatetime: string) {
  if (isoDatetime.length === 0) {
    return isoDatetime;
  }
  const date = new Date(isoDatetime);

  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  const hours = String(date.getHours()).padStart(2, "0");
  const minutes = String(date.getMinutes()).padStart(2, "0");

  // Format the date to the desired string
  const localDateTime = `${year}-${month}-${day}T${hours}:${minutes}`;

  return localDateTime;
}

function EventForm({
  initialData = {
    id: null,
    title: "",
    eventDatetime: "",
    description: "",
    eventImageUrl: "",
  },
  onSubmit,
}: EventFormProps) {
  const [title, setTitle] = useState(initialData.title);
  const [eventDatetime, setEventDatetime] = useState(
    convertToLocaleDatetime(initialData.eventDatetime)
  );
  const [description, setDescription] = useState(initialData.description);
  const [eventImageUrl, setEventImageUrl] = useState(initialData.eventImageUrl);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit({ title, eventDatetime, description, eventImageUrl });
  };

  return (
    <Box component="form" onSubmit={handleSubmit}>
      <TextField
        label="Event Title"
        value={title}
        onChange={(e) => setTitle(e.target.value)}
        required
      />
      <TextField
        type="datetime-local"
        label="Event Date"
        InputLabelProps={{ shrink: true }}
        value={eventDatetime}
        onChange={(e) => setEventDatetime(e.target.value)}
        required
      />
      <TextField
        label="Description"
        value={description}
        onChange={(e) => setDescription(e.target.value)}
        multiline
        rows={4}
        required
      />
      <TextField
        label="Event Image URL"
        value={eventImageUrl}
        onChange={(e) => setEventImageUrl(e.target.value)}
      />
      <Button variant="contained" type="submit">
        Submit
      </Button>
    </Box>
  );
}

export default EventForm;
