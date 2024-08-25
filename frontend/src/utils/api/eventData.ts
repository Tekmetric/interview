import { EventData, EventDataResponse } from "../../typings/eventData";
import { send } from "../send";

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

export async function getEvent(eventId: number): Promise<EventData> {
  return send<EventDataResponse>("GET", `/api/events/${eventId}`).then(
    (data: EventDataResponse) => ({
      id: data.id,
      title: data.title,
      eventDatetime: convertToLocaleDatetime(data.event_datetime),
      description: data.description,
      eventImageUrl: data.event_image_url,
    })
  );
}

export async function updateEvent(data: EventData) {
  return send("PATCH", `/api/events/${data.id}/`, {
    title: data.title,
    event_datetime: new Date(data.eventDatetime).toISOString(),
    description: data.description,
    event_image_url: data.eventImageUrl || null,
  });
}

export async function createEvent(data: Omit<EventData, "id">) {
  return send("POST", "/api/events/", {
    title: data.title,
    event_datetime: new Date(data.eventDatetime).toISOString(),
    description: data.description,
    event_image_url: data.eventImageUrl || null,
  });
}
