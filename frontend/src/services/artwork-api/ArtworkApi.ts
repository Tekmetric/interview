import { ArtworkGetListResponse } from '../../types/response/ArtworkGetListResponse'

export class ArtworkApi {
  public static readonly initialPage = 20 // It has some non-broken images
  private static readonly limit = 10
  private static readonly defaultImageWidthPx = 843
  private static readonly smallImageWidthPx = 450
  private static readonly artworkFieldsToRequest = [
    'id',
    'image_id',
    'title',
    'description',
    'date_display',
    'artist_title',
    'thumbnail'
  ].join(',')

  public static async getList({
    page
  }: {
    page: number
  }): Promise<ArtworkGetListResponse> {
    const response = await fetch(
      `https://api.artic.edu/api/v1/artworks?page=${page}&limit=${ArtworkApi.limit}&fields=${ArtworkApi.artworkFieldsToRequest}`
    )

    return await response.json()
  }

  public static getImageUrl(imageId: string, width: number) {
    const normalizedWidth =
      width <= ArtworkApi.defaultImageWidthPx
        ? ArtworkApi.smallImageWidthPx
        : ArtworkApi.defaultImageWidthPx

    return `https://www.artic.edu/iiif/2/${imageId}/full/${normalizedWidth},/0/default.jpg`
  }

  public static getFallbackImageUrl() {
    return '/missing_image.png'
  }
}
