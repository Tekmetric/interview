import React from 'react'

import { ExerciseDifficulty } from '../../types'

interface Props {
  selectedDifficulties: ExerciseDifficulty[]
  setSelectedDifficulties: (difficulties: ExerciseDifficulty[]) => void
}

export const DifficultyFilter: React.FC<Props> = ({
  selectedDifficulties,
  setSelectedDifficulties
}) => {
  const difficulties: ExerciseDifficulty[] = [
    'beginner',
    'intermediate',
    'expert'
  ]

  const handleToggle = (difficulty: ExerciseDifficulty) => {
    if (selectedDifficulties.includes(difficulty)) {
      setSelectedDifficulties(
        selectedDifficulties.filter(item => item !== difficulty)
      )
    } else {
      setSelectedDifficulties([...selectedDifficulties, difficulty])
    }
  }

  return (
    <div className='flex join'>
      {difficulties.map(difficulty => (
        <button
          key={difficulty}
          type='button'
          onClick={() => handleToggle(difficulty)}
          className={`btn px-4 join-item py-2 rounded-lg font-medium ${
            selectedDifficulties.includes(difficulty)
              ? 'bg-base-300'
              : 'bg-neutral'
          }`}
        >
          {difficulty.charAt(0).toUpperCase() + difficulty.slice(1)}
        </button>
      ))}
    </div>
  )
}

export default DifficultyFilter
