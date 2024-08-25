import React from "react";
import EventForm from "../components/EventForm";
import { useFormik } from "formik";
import { validationSchema } from "../typings/eventFormSchema";
import { EventData } from "../typings/eventData";
import { useCreateEventMutation } from "../utils/hooks.ts/eventData";

function CreatePage() {
  const createEventMutation = useCreateEventMutation();

  const formik = useFormik<EventData>({
    initialValues: {
      id: null,
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
  return <EventForm formik={formik} />;
}

export default CreatePage;
