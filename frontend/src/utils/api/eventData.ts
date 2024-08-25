import { EventData, EventDataResponse } from "../../typings/eventData";
import { convertToLocaleDatetime } from "../datetime";
import { send } from "../send";

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
