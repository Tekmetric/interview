import React from 'react'

import { ExerciseMuscle } from '../../types'
import { humanizeText } from '../utils'

interface Props {
  selectedMuscle: ExerciseMuscle | ''
  setSelectedMuscle: (muscle: ExerciseMuscle) => void
}

export const MuscleGroupSelect: React.FC<Props> = ({
  selectedMuscle,
  setSelectedMuscle
}) => {
  const muscles = Object.values(ExerciseMuscle)

  return (
    <select
      id='muscle-select'
      value={selectedMuscle}
      onChange={e => setSelectedMuscle(e.target.value as ExerciseMuscle)}
      className='select select-bordered max-w-xs w-72'
    >
      <option value='' disabled>
        Select muscle group
      </option>

      {muscles.map(muscle => (
        <option key={muscle} value={muscle}>
          {humanizeText(muscle)}
        </option>
      ))}
    </select>
  )
}
