import { z } from 'zod'

const ReviewCreateRequestSchema = z.object({
  rating: z.number().int().min(1).max(5),
  message: z.string(),
})

const ReviewsFiltersSchema = z.object({
  rating: z.number().int().min(1).max(5).nullish(),
  keyword: z.string().nullish(),
})

const ReviewSortBys = ['CREATED_AT', 'RATING'] as const
const ReviewSortBySchema = z.enum(ReviewSortBys)

const ReviewSortOrders = ['ASC', 'DESC'] as const
const ReviewSortOrdersSchema = z.enum(ReviewSortOrders)

const ReviewsSortingSchema = z.object({
  sortBy: ReviewSortBySchema,
  sortOrder: ReviewSortOrdersSchema,
})

const ReviewsPagingSchema = z.object({
  page: z.number().int(),
  size: z.number().int(),
})

type ReviewCreateRequest = z.infer<typeof ReviewCreateRequestSchema>

type ReviewsFilters = z.infer<typeof ReviewsFiltersSchema>
type ReviewsSorting = z.infer<typeof ReviewsSortingSchema>
type ReviewsPaging = z.infer<typeof ReviewsPagingSchema>

type ReviewSortBy = z.infer<typeof ReviewSortBySchema>
type ReviewSortOrder = z.infer<typeof ReviewSortOrdersSchema>

export type { ReviewCreateRequest, ReviewSortBy, ReviewSortOrder, ReviewsFilters, ReviewsPaging, ReviewsSorting }

export {
  ReviewCreateRequestSchema,
  ReviewSortBys,
  ReviewSortOrders,
  ReviewSortOrdersSchema,
  ReviewsPagingSchema,
  ReviewsSortingSchema,
}
