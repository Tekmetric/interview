import React from "react";
import { useNavigate } from "react-router-dom";
import EventForm from "../components/EventForm";
import { send } from "../utils/send";
import { useFormik } from "formik";
import { validationSchema } from "../typings/eventFormSchema";
import { EventData } from "../typings/eventData";

function CreatePage() {
  const navigate = useNavigate();

  const handleCreate = (data: {
    title: string;
    eventDatetime: string;
    description: string;
    evetImageUrl?: string;
  }) => {
    send("POST", "/api/events/", {
      title: data.title,
      event_datetime: new Date(data.eventDatetime).toISOString(),
      description: data.description,
      event_image_url: data.evetImageUrl || null,
    }).then(() => navigate("/"));
  };

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
      handleCreate(values);
    },
  });
  return <EventForm formik={formik} />;
}

export default CreatePage;
