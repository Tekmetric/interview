import React from 'react'

import { Exercise as ExerciseType } from '../../types'
import { ExerciseBadges } from '../ExerciseBadges/ExerciseBadges'
import { humanizeText } from '../utils'

export const Exercise = ({
  name,
  type,
  equipment,
  difficulty,
  instructions
}: ExerciseType) => (
  <div className='collapse collapse-plus bg-base-200'>
    <input type='checkbox' />

    <div className='flex justify-between items-center collapse-title text-xl font-medium'>
      <strong>{name}</strong>

      <ExerciseBadges type={type} difficulty={difficulty} />
    </div>

    <div className='collapse-content'>
      <div className='badge badge-outline'>
        Equipment needed: {humanizeText(equipment, false)}
      </div>
      <br />
      <br />
      {instructions}
    </div>
  </div>
)
