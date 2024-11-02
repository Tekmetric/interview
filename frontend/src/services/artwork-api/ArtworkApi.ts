import { ArtworkGetListResponse } from '../../types/response/ArtworkGetListResponse';

export class ArtworkApi {
  private static readonly limit = 10
  private static readonly defaultImageWidthPx = 843
  private static readonly smallImageWidthPx = 450

  public static async getList ({ page }: {
    page: number
  }): Promise<ArtworkGetListResponse> {
    const response = await fetch(`https://api.artic.edu/api/v1/artworks?page=${page}&limit=${ArtworkApi.limit}&fields=id,title,image_id,thumbnail`)

    return await response.json()
  }

  public static getImageUrl (imageId: string, width: number) {
    const normalizedWidth = width <= ArtworkApi.defaultImageWidthPx
      ? ArtworkApi.smallImageWidthPx
      : ArtworkApi.defaultImageWidthPx

    return `https://www.artic.edu/iiif/2/${imageId}/full/${normalizedWidth},/0/default.jpg`
  }

  public static getFallbackImageUrl () {
    return '/missing_image.png'
  }
}