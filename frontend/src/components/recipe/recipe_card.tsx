import { useMemo } from 'react'
import tw, { styled } from 'twin.macro'
import { Recipe } from '../../api/types/recipe/recipe'
import Avatar from '../avatar'
import Rating from '../rating'
import RecipeDuration from './recipe_duration'

const Container = styled.div({
  ...tw`h-[150px] lg:h-[200px]`,
  ...tw`flex flex-row rounded-[8px] overflow-hidden`,
  ...tw`shadow-md shadow-gray-500`,
})

type RecipeCardProps = {
  recipe: Recipe
}

const RecipeCard = ({ recipe }: RecipeCardProps): JSX.Element => {
  const formattedDate = useMemo(() => new Date(recipe.createdAt).toLocaleString(), [recipe.createdAt])

  return (
    <Container>
      <div tw="relative flex items-end justify-center w-[35%] h-full">
        <img tw="absolute w-full h-full object-cover z-10" src={recipe.image} />
        <div tw="p-[8px] rounded-[8px] bg-slate-800 mb-[12px] z-[11]">
          <Rating rating={recipe.ratingAverage} />
        </div>
      </div>

      <div tw="flex-1 flex flex-col space-y-[16px] justify-between p-[16px]">
        <div tw="flex flex-row space-x-[16px] justify-between items-center">
          <span tw="text-[16px] font-semibold">{recipe.mealType}</span>
          <RecipeDuration duration={recipe.duration} />
        </div>

        <span tw="text-[24px] font-semibold line-clamp-2">{recipe.title}</span>

        <div tw="flex flex-row space-x-[16px] justify-end lg:justify-between items-center">
          <span tw="hidden lg:block text-[12px] font-semibold">{formattedDate}</span>
          <Avatar variant="right" image={recipe.userInfo.avatar} name={recipe.userInfo.name} />
        </div>
      </div>
    </Container>
  )
}

export default RecipeCard
