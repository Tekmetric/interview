import { z } from 'zod'
import { MealTypesSchema } from './recipe'

const RecipeCreateRequestSchema = z.object({
  title: z.string(),
  description: z.string(),
  duration: z.number().int(),
  mealType: MealTypesSchema,
})

const RecipeUpdateRequestSchema = z.object({
  title: z.string(),
  description: z.string(),
  duration: z.number().int(),
  mealType: MealTypesSchema,
})

const RecipesFiltersSchema = z.object({
  userId: z.string().uuid().nullish(),
  keyword: z.string().nullish(),
  mealType: MealTypesSchema.nullish(),
})

const RecipeSortBys = ['CREATED_AT', 'RATING_AVERAGE', 'RATING_COUNT'] as const
const RecipeSortBySchema = z.enum(RecipeSortBys)

const RecipeSortOrders = ['ASC', 'DESC'] as const
const RecipeSortOrdersSchema = z.enum(RecipeSortOrders)

const RecipesSortingSchema = z.object({
  sortBy: RecipeSortBySchema,
  sortOrder: RecipeSortOrdersSchema,
})

const RecipesPagingSchema = z.object({
  page: z.number().int(),
  size: z.number().int(),
})

type RecipeCreateRequest = z.infer<typeof RecipeCreateRequestSchema>
type RecipeUpdateRequest = z.infer<typeof RecipeUpdateRequestSchema>

type RecipesFilters = z.infer<typeof RecipesFiltersSchema>
type RecipesSorting = z.infer<typeof RecipesSortingSchema>
type RecipesPaging = z.infer<typeof RecipesPagingSchema>

type RecipeSortBy = z.infer<typeof RecipeSortBySchema>
type RecipeSortOrder = z.infer<typeof RecipeSortOrdersSchema>

export type {
  RecipeCreateRequest,
  RecipeSortBy,
  RecipeSortOrder,
  RecipeUpdateRequest,
  RecipesFilters,
  RecipesPaging,
  RecipesSorting,
}

export {
  RecipeCreateRequestSchema,
  RecipeSortBys,
  RecipeSortOrders,
  RecipeSortOrdersSchema,
  RecipeUpdateRequestSchema,
  RecipesPagingSchema,
  RecipesSortingSchema,
}
