import axios from 'axios'

import { Exercise, ExerciseMuscle } from '../../../../types'

const importSampleData = async (
  muscle: ExerciseMuscle
): Promise<Exercise[]> => {
  try {
    const data = (await import(`../sample-data/${muscle}.json`)).default
    return data
  } catch (error) {
    throw new Error(`No data found for muscle group: ${muscle}`)
  }
}

export const fetchExercises = async (
  muscle: ExerciseMuscle
): Promise<Exercise[]> => {
  if (!process.env.REACT_APP_RAPIDAPI_KEY) {
    // Fall back to local JSON data when API key is not present
    return await importSampleData(muscle)
  }

  const { data } = await axios.get<Exercise[]>('/api/exercises', {
    params: { muscle }
  })

  return data
}
