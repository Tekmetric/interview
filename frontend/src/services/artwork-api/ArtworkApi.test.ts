import {
  ArtworkGetListResponse,
  ArtworkListItemThumbnail
} from '../../types/response/ArtworkGetListResponse'
import { ArtworkApi } from './ArtworkApi'

describe('ArtworkApi', () => {
  afterEach(() => {
    jest.clearAllMocks()
  })

  describe('getList', () => {
    beforeEach(() => {
      global.fetch = jest.fn()
    })

    it('should fetch artworks list and return JSON response', async () => {
      const mockPage = 1
      const mockResponseData: ArtworkGetListResponse = {
        data: [
          {
            id: 1,
            image_id: 'abcd1234',
            title: 'Artwork Title',
            description: 'Artwork Description',
            date_display: '2021',
            artist_title: 'Artist Name',
            thumbnail: {} as ArtworkListItemThumbnail
          }
        ],
        pagination: {
          total: 100,
          limit: 10,
          offset: 0,
          total_pages: 10,
          current_page: 1
        }
      }

      ;(global.fetch as jest.Mock).mockResolvedValue({
        json: jest.fn().mockResolvedValue(mockResponseData)
      })

      const response = await ArtworkApi.getList({ page: mockPage })

      expect(global.fetch).toHaveBeenCalledWith(
        `https://api.artic.edu/api/v1/artworks?page=${mockPage}&limit=10&fields=id,image_id,title,description,date_display,artist_title,thumbnail`
      )

      expect(response).toEqual(mockResponseData)
    })
  })

  describe('getImageUrl', () => {
    it('should return correct image URL with default width when width is greater than defaultImageWidthPx', () => {
      const imageId = 'abcd1234'
      const width = 900
      const expectedUrl = `https://www.artic.edu/iiif/2/${imageId}/full/843,/0/default.jpg`

      const result = ArtworkApi.getImageUrl(imageId, width)

      expect(result).toBe(expectedUrl)
    })

    it('should return correct image URL with small width when width is less than or equal to defaultImageWidthPx', () => {
      const imageId = 'abcd1234'
      const width = 400
      const expectedUrl = `https://www.artic.edu/iiif/2/${imageId}/full/450,/0/default.jpg`

      const result = ArtworkApi.getImageUrl(imageId, width)

      expect(result).toBe(expectedUrl)
    })
  })
})
