export interface Exercise {
  name: string
  type: ExerciseType
  muscle: ExerciseMuscle
  equipment: string
  difficulty: ExerciseDifficulty
  instructions: string
}

export enum ExerciseType {
  Cardio = 'cardio',
  OlympicWeightlifting = 'olympic_weightlifting',
  Plyometrics = 'plyometrics',
  Powerlifting = 'powerlifting',
  Strength = 'strength',
  Stretching = 'stretching',
  Strongman = 'strongman'
}

export enum ExerciseMuscle {
  Abdominals = 'abdominals',
  Abductors = 'abductors',
  Adductors = 'adductors',
  Biceps = 'biceps',
  Calves = 'calves',
  Chest = 'chest',
  Forearms = 'forearms',
  Glutes = 'glutes',
  Hamstrings = 'hamstrings',
  Lats = 'lats',
  LowerBack = 'lower_back',
  MiddleBack = 'middle_back',
  Neck = 'neck',
  Quadriceps = 'quadriceps',
  Traps = 'traps',
  Triceps = 'triceps'
}

export type ExerciseDifficulty = 'beginner' | 'intermediate' | 'expert'
