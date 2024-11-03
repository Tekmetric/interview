import { ArtworkListItem } from '../../../../types/artwork-list-item'
import { ArtworkGetListResponse } from '../../../../types/response/ArtworkGetListResponse'

export const buildArtworkData = (
  artworkDataPages: ArtworkGetListResponse[]
) => {
  const imageIdsSet = new Set<string>()
  const flattenData = artworkDataPages.flatMap(page => page.data) ?? []

  return flattenData.reduce(
    (
      acc,
      {
        id,
        image_id,
        title,
        description,
        date_display,
        artist_title,
        thumbnail
      }
    ) => {
      if (!image_id || !thumbnail) {
        return acc
      }

      if (imageIdsSet.has(image_id)) {
        return acc
      }

      // To avoid duplicated ids in the case when requesting a new page,
      // and it contains some ids from the current page (because of new artworks were added)
      imageIdsSet.add(image_id)

      acc.push({
        id,
        imageId: image_id,
        title,
        description,
        date: date_display,
        artist: artist_title,
        altText: thumbnail.alt_text,
        blurDataURL: thumbnail.lqip,
        originalWidth: thumbnail.width,
        originalHeight: thumbnail.height,
        aspectRatio: thumbnail.width / thumbnail.height
      })

      return acc
    },
    [] as ArtworkListItem[]
  )
}
