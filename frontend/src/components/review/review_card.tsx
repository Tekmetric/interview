import { Review } from '../../api/types/review/review'
import Avatar from '../avatar'
import Rating from '../rating'

type ReviewCardProps = {
  review: Review
}

const ReviewCard = ({ review }: ReviewCardProps): JSX.Element => {
  return (
    <div tw="flex flex-col space-y-[8px] p-[8px]">
      <div tw="flex flex-row items-center space-x-[8px]">
        <Rating rating={review.rating} />
        <span tw="text-[12px]">{new Date(review.createdAt).toLocaleString()}</span>
      </div>
      <Avatar variant="left" image={review.userInfo.avatar} name={review.userInfo.name} size={24} />
      <p tw="text-[14px] ">{review.message}</p>
    </div>
  )
}

export default ReviewCard
