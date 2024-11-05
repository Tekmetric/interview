import { act, renderHook } from '@testing-library/react'

import { ExerciseMuscle } from '../../../types'
import useExerciseFilters from './use-exercise-filters'

describe('useExerciseFilters', () => {
  it('should initialize with default muscle and difficulty selections', () => {
    const { result } = renderHook(() => useExerciseFilters())

    expect(result.current.selectedMuscle).toBe('')
    expect(result.current.selectedDifficulties).toEqual([
      'beginner',
      'intermediate',
      'expert'
    ])
  })

  it('should update selectedMuscle when setSelectedMuscle is called', () => {
    const { result } = renderHook(() => useExerciseFilters())

    act(() => {
      result.current.setSelectedMuscle(ExerciseMuscle.Chest)
    })

    expect(result.current.selectedMuscle).toBe(ExerciseMuscle.Chest)
  })

  it('should update selectedDifficulties when setSelectedDifficulties is called', () => {
    const { result } = renderHook(() => useExerciseFilters())

    act(() => {
      result.current.setSelectedDifficulties(['beginner'])
    })

    expect(result.current.selectedDifficulties).toEqual(['beginner'])
  })
})
