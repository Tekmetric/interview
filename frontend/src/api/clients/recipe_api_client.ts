import { Recipe } from '../types/recipe/recipe'
import {
  RecipesFilters,
  RecipesPaging,
  RecipesSorting
} from '../types/recipe/recipe_payloads'
import { DataResponse, PagedResponse } from '../types/response_payloads'
import { axios_instance } from './_axios_client'

class RecipeApiClient {
  //
  static async fetchAllBy(
    filters: RecipesFilters,
    sorting: RecipesSorting,
    paging: RecipesPaging,
  ): Promise<PagedResponse<Recipe>> {
    const result = await axios_instance.get<PagedResponse<Recipe>>('/recipes', {
      params: { ...filters, ...sorting, ...paging },
    })

    return result.data
  }

  static async fetchOneBy(id: string): Promise<DataResponse<Recipe>> {
    const result = await axios_instance.get<DataResponse<Recipe>>(`/recipes/${id}`)

    return result.data
  }
}

export { RecipeApiClient }
