import React from "react";
import { TextField, Button, Box } from "@mui/material";
import { FormikProps } from "formik";
import { EventData } from "../typings/eventData";
import styled from "@emotion/styled";
import { getCurrentDatetimeLocal } from "../utils/datetime";

interface EventFormProps {
  formik: FormikProps<EventData>;
}

const FormContainer = styled(Box)`
  display: flex;
  flex-direction: column;
  gap: 32px;
  max-width: 400px;
  margin: 30px auto;
  padding: 8px;
`;

function EventForm({ formik }: EventFormProps) {
  return (
    <form
      onSubmit={formik.handleSubmit}
      style={{ maxWidth: "400px", width: "100%" }}
    >
      <FormContainer>
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
          inputProps={{ min: getCurrentDatetimeLocal() }}
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
          InputLabelProps={{ shrink: true }}
          value={formik.values.eventImageUrl}
          onChange={formik.handleChange}
          error={formik.touched.eventImageUrl && !!formik.errors.eventImageUrl}
        />
        <Button variant="contained" type="submit">
          Submit
        </Button>
      </FormContainer>
    </form>
  );
}

export default EventForm;
