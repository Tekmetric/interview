import { EventData } from "../../typings/eventData";
import { convertToLocaleDatetime } from "../datetime";
import { request } from "../request";

export type EventDataResponse = {
  id: number;
  title: string;
  event_datetime: string;
  description: string;
  event_image_url: string;
};

export type PaginatedEventDataResult = {
  count: number;
  next: string; // URL
  results: EventDataResponse[];
};

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

function sleep(ms: number): Promise<void> {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

export async function fetchEvents({
  pageParam = 1,
  filterStartDate,
  filterEndDate,
}: {
  pageParam?: number | undefined | unknown;
  filterStartDate?: string | undefined;
  filterEndDate?: string | undefined;
}): Promise<PaginatedEventDataResult> {
  const params = new URLSearchParams();
  await sleep(3000);
  if (pageParam) params.append("page", pageParam.toString());
  if (filterStartDate) params.append("start_date", filterStartDate);
  if (filterEndDate) params.append("end_date", filterEndDate);

  return request<PaginatedEventDataResult>(
    "GET",
    "/api/events/",
    undefined,
    params
  );
}
