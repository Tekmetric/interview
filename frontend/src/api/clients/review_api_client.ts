import { PagedResponse } from '../types/response_payloads'
import { Review } from '../types/review/review'
import { ReviewsFilters, ReviewsPaging, ReviewsSorting } from '../types/review/review_payloads'
import { axios_instance } from './_axios_client'

class ReviewApiClient {
  //
  static async fetchAllBy(
    recipeId: string,
    filters: ReviewsFilters,
    sorting: ReviewsSorting,
    paging: ReviewsPaging,
  ): Promise<PagedResponse<Review>> {
    const result = await axios_instance.get<PagedResponse<Review>>(`/recipes/${recipeId}/reviews`, {
      params: { ...filters, ...sorting, ...paging },
    })

    return result.data
  }
}

export { ReviewApiClient }
