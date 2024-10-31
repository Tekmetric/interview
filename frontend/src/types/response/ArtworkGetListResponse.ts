type ArtworkListItem = {
  id: number;
  image_id: string | null;
  thumbnail: {
    lqip: string;
    alt_text: string;
    width: number
  } | null;
  title: string;
}

export type ArtworkPaginationData = {
  total: number;
  limit: number;
  offset: number;
  total_pages: number;
  current_page: number
}

export type ArtworkGetListResponse = {
  pagination: ArtworkPaginationData;
  data: ArtworkListItem[];
}