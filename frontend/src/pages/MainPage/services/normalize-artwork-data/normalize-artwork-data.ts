import { ArtworkListItem } from '../../../../types/artwork-list-item';
import { ArtworkGetListResponse } from '../../../../types/response/ArtworkGetListResponse';

export const normalizeArtworkData = (artworkDataPages: ArtworkGetListResponse[]) => {
  const imageIdsSet = new Set<string>()
  const flattenData = artworkDataPages.flatMap((page) => page.data) ?? []

  return flattenData.reduce((acc, {
    id,
    image_id,
    title,
    description,
    date_display,
    artist_title,
    thumbnail
  }) => {
    if (!image_id || !thumbnail) {
      return acc;
    }

    if (imageIdsSet.has(image_id)) {
      return acc
    }

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
      originalHeight: thumbnail.height
    })

    return acc
  }, [] as ArtworkListItem[])
}