import React, { FC } from 'react'

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
    <div className='flex flex-col items-center min-h-screen px-4 md:px-8'>
      <div className='hidden md:block'>
        <Logo />
      </div>

      <div className='flex flex-col gap-6 w-full max-w-4xl mt-10 md:mt-20'>
        <div className='flex flex-col md:flex-row gap-6 md:gap-20 justify-between items-center'>
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
