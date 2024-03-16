import { useEffect, useState } from 'react'
import useSWR from 'swr'
import tw, { styled } from 'twin.macro'
import { ReviewApiClient } from '../../api/clients/review_api_client'
import { Recipe } from '../../api/types/recipe/recipe'
import { ReviewsFilters, ReviewsPaging, ReviewsSorting } from '../../api/types/review/review_payloads'
import Dropdown from '../dropdown'
import Pagination from '../pagination'
import SearchField from '../search_field'
import ReviewCard from './review_card'

const Container = styled.div({
  ...tw`m-[8px]`,
  ...tw`flex flex-col divide-y`,
  ...tw`border-[2px] border-black rounded-[8px]`,
})

const FiltersContainer = styled.div({
  ...tw`w-full bg-slate-800`,
})

const ListContainer = styled.div({
  ...tw`p-[16px] h-[400px] lg:h-[600px] overflow-auto`,
  ...tw`flex flex-col space-y-[16px] divide-y`,
})

const PaginationContainer = styled.div({
  ...tw`w-full p-[16px] flex justify-end`,
})

type ReviewsListProps = {
  recipe: Recipe
}

const ReviewsList = ({ recipe }: ReviewsListProps): JSX.Element => {
  const [filters, setFilters] = useState<ReviewsFilters>({})
  const [sorting, setSorting] = useState<ReviewsSorting>({
    sortBy: 'CREATED_AT',
    sortOrder: 'DESC',
  })
  const [paging, setPaging] = useState<ReviewsPaging>({ page: 0, size: 10 })

  const { data, isLoading } = useSWR([recipe.id, filters, sorting, paging], () =>
    ReviewApiClient.fetchAllBy(recipe.id, filters, sorting, paging),
  )

  useEffect(() => {
    setPaging(p => ({ ...p, page: 0 }))
  }, [filters])

  return (
    <Container>
      <FiltersContainer>
        <div tw="p-[16px] flex flex-col lg:flex-row gap-[16px]">
          <SearchField
            debounce={300}
            placeholder={'Search'}
            onChange={keyword => setFilters(f => ({ ...f, keyword }))}
          />
          <Dropdown<ReviewsSorting>
            defaultKey={'newest_first'}
            options={{
              newest_first: { label: 'Newest First', value: { sortBy: 'CREATED_AT', sortOrder: 'DESC' } },
              oldest_first: { label: 'Oldest First', value: { sortBy: 'CREATED_AT', sortOrder: 'ASC' } },
              highest_rating: { label: 'Highest Rating', value: { sortBy: 'RATING', sortOrder: 'DESC' } },
            }}
            onChange={v => setSorting(v!)}
          />
        </div>
      </FiltersContainer>
      <ListContainer>
        {/* TODO EMPTY REVIEWS MAYBE? */}
        {data && data.data.map(review => <ReviewCard key={review.id} review={review} />)}
      </ListContainer>

      <PaginationContainer>
        <Pagination
          variant="small"
          page={data?.page}
          totalPages={data?.totalPages}
          onPageChange={page => setPaging(p => ({ ...p, page }))}
        />
      </PaginationContainer>
    </Container>
  )
}

export default ReviewsList
