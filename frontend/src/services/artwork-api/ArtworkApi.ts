import { ArtworkGetListResponse } from '../../types/response/ArtworkGetListResponse';

export class ArtworkApi {
  private static readonly limit = 10

  public static async getList ({}: {
    page: number
  }): Promise<ArtworkGetListResponse> {
    const response = await fetch(`https://api.artic.edu/api/v1/artworks?page=1&limit=${ArtworkApi.limit}&fields=id,title,image_id,thumbnail`)

    return await response.json()
  }

  public static getImageUrl (imageId: string) {
    return `https://www.artic.edu/iiif/2/${imageId}/full/843,/0/default.jpg`
  }
}