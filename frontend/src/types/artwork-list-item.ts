export type ArtworkListItem = {
  id: number
  imageId: string
  title: string
  description: string | null
  date: string | null
  artist: string | null
  altText: string
  blurDataURL: string
  originalWidth: number
  originalHeight: number
}
