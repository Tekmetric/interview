import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import EventForm from "../components/EventForm";
import { send } from "../utils/send";
import { CircularProgress } from "@mui/material";
import { EventData, EventDataResponse } from "../typings/eventData";
import { useFormik } from "formik";
import { validationSchema } from "../typings/eventFormSchema";

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

const DEFAULT_INITIAL_VALUES: EventData = {
  id: null,
  title: "",
  eventDatetime: "",
  description: "",
  eventImageUrl: "",
};

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
          eventDatetime: convertToLocaleDatetime(data.event_datetime),
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
    send("PUT", `/api/events/${id}/`, {
      title: data.title,
      event_datetime: new Date(data.eventDatetime).toISOString(),
      description: data.description,
      event_image_url: data.eventImageUrl || null,
    }).then(() => navigate("/"));
  };

  const formik = useFormik<EventData>({
    initialValues: eventData ? eventData : DEFAULT_INITIAL_VALUES,
    validationSchema,
    enableReinitialize: true,
    onSubmit: (values) => {
      handleUpdate(values);
    },
  });

  if (!eventData) {
    return <CircularProgress />;
  }

  return <EventForm formik={formik} />;
}

export default DetailsPage;
