import React from "react";
import EventForm from "../components/EventForm";
import { useFormik } from "formik";
import { validationSchema } from "../typings/eventFormSchema";
import { EventData } from "../typings/eventData";
import { useCreateEventMutation } from "../utils/hooks/eventData";
import styled from "@emotion/styled";
import { Box, Typography } from "@mui/material";

const PageContainer = styled(Box)`
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  height: 100vh;
  justify-content: flex-start;
  padding-top: 32px;
`;

function CreatePage() {
  const createEventMutation = useCreateEventMutation();

  const formik = useFormik<EventData>({
    initialValues: {
      title: "",
      eventDatetime: "",
      description: "",
      eventImageUrl: "",
    },
    validationSchema,
    onSubmit: (values) => {
      createEventMutation.mutate(values);
    },
  });
  return (
    <PageContainer>
      <Typography variant="h4">Create an event</Typography>
      <EventForm formik={formik} />
    </PageContainer>
  );
}

export default CreatePage;
