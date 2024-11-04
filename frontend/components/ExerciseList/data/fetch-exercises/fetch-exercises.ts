import axios from 'axios'

import { Exercise, ExerciseMuscle } from '../../../types'

export const fetchExercises = async (
  muscle: ExerciseMuscle
): Promise<Exercise[]> => {
  const { data } = await axios.get<Exercise[]>('/api/exercises', {
    params: { muscle }
  })

  return data
}
