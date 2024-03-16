import { useParams } from 'react-router-dom'
import useSWR from 'swr'
import { RecipeApiClient } from '../api/clients/recipe_api_client'
import RecipeInfo from '../components/recipe/recipe_info'
import ReviewsList from '../components/review/reviews_list'
import PageLayout from '../layouts/page_layout'

const RecipeDetailsPage = (): JSX.Element => {
  const { id } = useParams()

  const { data } = useSWR(['recipe', id], () => (id == null ? null : RecipeApiClient.fetchOneBy(id)))

  return (
    <PageLayout>
      {/* TODO LOADING SKELETON MAYBE ?? */}
      {data && <RecipeInfo recipe={data.data} />}

      {data && <ReviewsList recipe={data.data} />}
    </PageLayout>
  )
}

export default RecipeDetailsPage
