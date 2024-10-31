type ArtworkListItem = {
  id: number;
  image_id: string | null;
  thumbnail: {
    lqip: string;
    alt_text: string;
  } | null;
  title: string;
}

export type ArtworkGetListResponse = {
  data: ArtworkListItem[]
}