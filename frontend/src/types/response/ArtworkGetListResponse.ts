export type ArtworkListItem = {
  id: number
  image_id: string | null
  title: string
  description: string | null
  date_display: string | null
  artist_title: string | null
  thumbnail: ArtworkListItemThumbnail | null
}

export type ArtworkListItemThumbnail = {
  lqip: string
  alt_text: string
  width: number
  height: number
}

export type ArtworkPaginationData = {
  total: number
  limit: number
  offset: number
  total_pages: number
  current_page: number
}

export type ArtworkGetListResponse = {
  pagination: ArtworkPaginationData
  data: ArtworkListItem[]
}
