import MarkdownPreview from '@uiw/react-markdown-preview'
import tw, { styled } from 'twin.macro'
import { Recipe } from '../../api/types/recipe/recipe'
import Avatar from '../avatar'
import Rating from '../rating'

const Container = styled.div({
  ...tw`flex-1 flex flex-col space-y-[16px]`,
})

type RecipeInfoProps = {
  recipe: Recipe
}

const RecipeInfo = ({ recipe }: RecipeInfoProps): JSX.Element => {
  return (
    <Container>
      <img tw="h-[400px] object-cover" src={recipe.image} />

      <div tw="flex flex-col space-y-[24px] px-[36px]">
        <div tw="flex flex-row justify-between items-center">
          <span tw="text-[16px] font-bold">{recipe.mealType}</span>
          <Rating rating={recipe.ratingAverage} total={recipe.ratingCount} />
        </div>
        <span tw="text-[16px] font-bold">{recipe.title}</span>
        <div tw="flex flex-row justify-between items-center">
          <span tw="text-[12px]">{new Date(recipe.createdAt).toLocaleString()}</span>
          <Avatar variant="right" image={recipe.userInfo.avatar} name={recipe.userInfo.name} />
        </div>
      </div>

      <div tw="p-[24px]" data-color-mode={'light'}>
        <MarkdownPreview source={recipe.description} css={[tw`p-[16px] min-h-[200px]`]} />
      </div>
    </Container>
  )
}

export default RecipeInfo
