import React from "react";
import { TextField, Button } from "@mui/material";
import { FormikProps } from "formik";
import { EventData } from "../typings/eventData";

interface EventFormProps {
  formik: FormikProps<EventData>;
}

// const formStyles = css`
//   display: flex,
//   flexDirection: column,
//   gap: 16px,
//   maxWidth: 400px,
//   margin: 0 auto,
// `;

function EventForm({ formik }: EventFormProps) {
  return (
    <form onSubmit={formik.handleSubmit}>
      <TextField
        id="title"
        name="title"
        label="Event Title"
        value={formik.values.title}
        onChange={formik.handleChange}
        error={formik.touched.title && !!formik.errors.title}
        required
      />
      <TextField
        id="eventDatetime"
        name="eventDatetime"
        type="datetime-local"
        label="Event Date"
        InputLabelProps={{ shrink: true }}
        value={formik.values.eventDatetime}
        onChange={formik.handleChange}
        error={formik.touched.eventDatetime && !!formik.errors.eventDatetime}
        required
      />
      <TextField
        id="description"
        name="description"
        label="Description"
        value={formik.values.description}
        onChange={formik.handleChange}
        error={formik.touched.description && !!formik.errors.description}
        multiline
        rows={4}
        required
      />
      <TextField
        id="eventImageUrl"
        name="eventImageUrl"
        label="Event Image URL"
        value={formik.values.eventImageUrl}
        onChange={formik.handleChange}
        error={formik.touched.eventImageUrl && !!formik.errors.eventImageUrl}
      />
      <Button variant="contained" type="submit">
        Submit
      </Button>
    </form>
  );
}

export default EventForm;
