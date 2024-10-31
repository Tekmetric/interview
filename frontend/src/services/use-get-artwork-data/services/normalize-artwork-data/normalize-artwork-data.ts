import { ArtworkListItem } from '../../../../types/artwork-list-item';
import { ArtworkGetListResponse } from '../../../../types/response/ArtworkGetListResponse';

export const normalizeArtworkData = (artworkDataPages: ArtworkGetListResponse[]) => {
  const imageIdsSet = new Set<string>()
  const flattenData = artworkDataPages.flatMap((page) => page.data) ?? []

  return flattenData.reduce((acc, {
    id,
    image_id,
    thumbnail,
    title
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
      altText: thumbnail.alt_text,
      blurDataURL: thumbnail.lqip
    })

    return acc
  }, [] as ArtworkListItem[])
}