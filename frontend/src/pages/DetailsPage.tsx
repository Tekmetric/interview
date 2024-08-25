import React from "react";
import { useParams } from "react-router-dom";
import EventForm from "../components/EventForm";
import { CircularProgress } from "@mui/material";
import { EventData } from "../typings/eventData";
import { useFormik } from "formik";
import { validationSchema } from "../typings/eventFormSchema";
import { useGetEvent, useUpdateEventMutation } from "../utils/hooks/eventData";

const DEFAULT_INITIAL_VALUES: EventData = {
  id: null,
  title: "",
  eventDatetime: "",
  description: "",
  eventImageUrl: "",
};

function DetailsPage() {
  const { id } = useParams<{ id: string }>();
  const eventId = parseInt(id!);
  const { data: eventData, isLoading } = useGetEvent(eventId);
  const updateEventMutation = useUpdateEventMutation();

  const formik = useFormik<EventData>({
    initialValues: eventData ? eventData : DEFAULT_INITIAL_VALUES,
    validationSchema,
    enableReinitialize: true,
    onSubmit: (values) => {
      updateEventMutation.mutate(values);
    },
  });

  if (isLoading) {
    return <CircularProgress />;
  }

  return <EventForm formik={formik} />;
}

export default DetailsPage;
