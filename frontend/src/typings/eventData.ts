export type EventDataResponse = {
  id: number;
  title: string;
  event_datetime: string;
  description: string;
  event_image_url: string;
};

export type EventData = {
  id: number | null;
  title: string;
  eventDatetime: string;
  description: string;
  eventImageUrl?: string;
};
