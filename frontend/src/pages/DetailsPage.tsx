import React from "react";
import { useParams } from "react-router-dom";
import EventForm from "../components/EventForm";
import { Box, CircularProgress, Typography } from "@mui/material";
import { EventData } from "../typings/eventData";
import { useFormik } from "formik";
import { validationSchema } from "../typings/eventFormSchema";
import { useGetEvent, useUpdateEventMutation } from "../utils/hooks/eventData";
import styled from "@emotion/styled";

const DEFAULT_INITIAL_VALUES: EventData = {
  title: "",
  eventDatetime: "",
  description: "",
  eventImageUrl: "",
};

const PageContainer = styled(Box)`
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  height: 100vh;
  justify-content: flex-start;
  padding-top: 32px;
`;

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
    return (
      <PageContainer>
        <CircularProgress />
      </PageContainer>
    );
  }

  return (
    <PageContainer>
      <Typography variant="h4">Event Details</Typography>
      <EventForm formik={formik} />
    </PageContainer>
  );
}

export default DetailsPage;
