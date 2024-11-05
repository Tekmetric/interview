import { ExerciseDifficulty, ExerciseType } from '../../types'
import { humanizeText } from '../utils'

const DIFFICULTY_BADGE_COLOR_MAPPING: Record<ExerciseDifficulty, string> = {
  beginner: 'badge-success',
  intermediate: 'badge-warning',
  expert: 'badge-error'
}

export const ExerciseBadges = ({
  type,
  difficulty
}: {
  type: ExerciseType
  difficulty: ExerciseDifficulty
}) => (
  <div className='flex gap-4'>
    <div className='badge badge-outline badge-primary min-w-32'>
      {humanizeText(type)}
    </div>

    <div
      className={`badge badge-outline ${DIFFICULTY_BADGE_COLOR_MAPPING[difficulty]} w-32`}
    >
      {humanizeText(difficulty)}
    </div>
  </div>
)
