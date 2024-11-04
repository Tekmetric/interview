import React, { FC, useState } from 'react'

import { DifficultyFilter } from '../components/DifficultyFilter/DifficultyFilter'
import ExerciseList from '../components/ExerciseList/ExerciseList'
import { Logo } from '../components/Logo/Logo'
import { MuscleGroupSelect } from '../components/MuscleGroupSelect/MuscleGroupSelect'
import useExerciseFilters from './hooks/use-exercise-filters/use-exercise-filters'

const Home: FC = () => {
  const {
    selectedMuscle,
    setSelectedMuscle,
    selectedDifficulties,
    setSelectedDifficulties
  } = useExerciseFilters()

  return (
    <div className='flex flex-col items-center min-h-screen'>
      <Logo />

      <div className='flex flex-col gap-6 w-full max-w-4xl mt-20 px-8'>
        <div className='flex gap-20 justify-between'>
          <MuscleGroupSelect
            selectedMuscle={selectedMuscle}
            setSelectedMuscle={setSelectedMuscle}
          />

          <DifficultyFilter
            selectedDifficulties={selectedDifficulties}
            setSelectedDifficulties={setSelectedDifficulties}
          />
        </div>

        <div className='mt-10'>
          {selectedMuscle && selectedDifficulties.length > 0 && (
            <ExerciseList
              muscle={selectedMuscle}
              selectedDifficulties={selectedDifficulties}
            />
          )}
        </div>
      </div>
    </div>
  )
}

export default Home
