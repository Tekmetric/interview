import { Icon } from '@iconify/react'

type RatingType = {
  rating: number
  total?: number
}

const Rating = ({ rating, total }: RatingType): JSX.Element => {
  return (
    <div tw="w-fit flex flex-row space-x-[4px] items-center">
      <Icon icon={rating > 0 ? 'ph-star-fill' : 'ph-star'} color="rgb(234 88 12)" />
      <Icon icon={rating >= 1 ? 'ph-star-fill' : 'ph-star'} color="rgb(234 88 12)" />
      <Icon icon={rating >= 2 ? 'ph-star-fill' : 'ph-star'} color="rgb(234 88 12)" />
      <Icon icon={rating >= 3 ? 'ph-star-fill' : 'ph-star'} color="rgb(234 88 12)" />
      <Icon icon={rating >= 4 ? 'ph-star-fill' : 'ph-star'} color="rgb(234 88 12)" />
      {total != null && <span tw="pl-[4px] font-semibold">{total}</span>}
    </div>
  )
}

export default Rating
