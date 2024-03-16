import { Icon } from '@iconify/react'

type RecipeDuration = {
  duration: number
}

const RecipeDuration = ({ duration }: RecipeDuration): JSX.Element => {
  return (
    <div tw="flex flex-row space-x-[8px] items-center p-[4px] rounded-[8px] bg-orange-500">
      <Icon icon="pajamas:timer" width={18} color="black" />

      <span tw="text-[14px] font-semibold">{duration} mins</span>
    </div>
  )
}

export default RecipeDuration
