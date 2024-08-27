import { EventData, EventDataResponse } from "../../typings/eventData";
import { convertToLocaleDatetime } from "../datetime";
import { request } from "../request";

export async function getEvent(eventId: number): Promise<EventData> {
  return request<EventDataResponse>("GET", `/api/events/${eventId}`).then(
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
  return request("PATCH", `/api/events/${data.id}/`, {
    title: data.title,
    event_datetime: new Date(data.eventDatetime).toISOString(),
    description: data.description,
    event_image_url: data.eventImageUrl || null,
  });
}

export async function createEvent(data: Omit<EventData, "id">) {
  return request("POST", "/api/events/", {
    title: data.title,
    event_datetime: new Date(data.eventDatetime).toISOString(),
    description: data.description,
    event_image_url: data.eventImageUrl || null,
  });
}
