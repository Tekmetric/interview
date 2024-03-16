import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import useSWR from 'swr'
import tw, { styled } from 'twin.macro'
import { RecipeApiClient } from '../api/clients/recipe_api_client'
import { MealType, MealTypes } from '../api/types/recipe/recipe'
import { RecipesFilters, RecipesPaging, RecipesSorting } from '../api/types/recipe/recipe_payloads'
import Dropdown from '../components/dropdown'
import Pagination from '../components/pagination'
import RecipeCard from '../components/recipe/recipe_card'
import SearchField from '../components/search_field'
import PageLayout from '../layouts/page_layout'

const FilterContainer = styled.div({
  ...tw`sticky top-[88px] z-50`,

  ...tw`bg-slate-800`,
})

const ListContainer = styled.div({
  ...tw`flex-1 grid`,
  ...tw`p-[8px] grid-cols-1 gap-[8px]`,
  ...tw`lg:(p-[44px] grid-cols-2 gap-[16px])`,
})

const PaginationContainer = styled.div({
  ...tw`min-h-[44px] mt-[24px] mb-[24px]`,
  ...tw`flex justify-center items-center`,
})

const RecipesPage = (): JSX.Element => {
  const [filters, setFilters] = useState<RecipesFilters>({})
  const [sorting, setSorting] = useState<RecipesSorting>({
    sortBy: 'CREATED_AT',
    sortOrder: 'DESC',
  })
  const [paging, setPaging] = useState<RecipesPaging>({ page: 0, size: 10 })

  const { data, isLoading } = useSWR([filters, sorting, paging], () => {
    return RecipeApiClient.fetchAllBy(filters, sorting, paging)
  })

  useEffect(() => {
    window.scrollTo({ top: 0, left: 0, behavior: 'smooth' })
  }, [paging, filters, sorting])

  useEffect(() => {
    setPaging(p => ({ ...p, page: 0 }))
  }, [filters])

  return (
    <PageLayout>
      <FilterContainer>
        <div tw="p-[16px] flex flex-row space-x-[16px]">
          <Dropdown<MealType>
            placeholder={'Meal Type'}
            clearable
            options={MealTypes.reduce((p, c) => ({ ...p, [c]: { label: c, value: c } }), {})}
            onChange={v => setFilters(f => ({ ...f, mealType: v }))}
          />
          <SearchField
            debounce={300}
            placeholder={'Search'}
            onChange={keyword => setFilters(f => ({ ...f, keyword }))}
          />
        </div>

        <div tw="p-[16px] flex flex-row justify-end space-x-[8px]">
          <Dropdown<RecipesSorting>
            defaultKey={'newest_first'}
            options={{
              newest_first: { label: 'Newest First', value: { sortBy: 'CREATED_AT', sortOrder: 'DESC' } },
              oldest_first: { label: 'Oldest First', value: { sortBy: 'CREATED_AT', sortOrder: 'ASC' } },
              highest_rating: { label: 'Highest Rating', value: { sortBy: 'RATING_AVERAGE', sortOrder: 'DESC' } },
              most_popular: { label: 'Most Popular', value: { sortBy: 'RATING_COUNT', sortOrder: 'DESC' } },
            }}
            onChange={v => setSorting(v!)}
          />
        </div>
      </FilterContainer>
      <ListContainer>
        {/*  */}
        {data &&
          data.data.map(recipe => (
            <Link key={recipe.id} to={recipe.id}>
              <RecipeCard recipe={recipe} />
            </Link>
          ))}
      </ListContainer>
      <PaginationContainer>
        <Pagination
          variant="normal"
          page={data?.page}
          totalPages={data?.totalPages}
          onPageChange={page => setPaging(p => ({ ...p, page }))}
        />
      </PaginationContainer>
    </PageLayout>
  )
}

export default RecipesPage
