import { useState } from 'react';
import { ExerciseDifficulty, ExerciseMuscle } from '../../../types';

const useExerciseFilters = () => {
	const [selectedMuscle, setSelectedMuscle] = useState<ExerciseMuscle | ''>('');
	const [selectedDifficulties, setSelectedDifficulties] = useState<
		ExerciseDifficulty[]
	>(['beginner', 'intermediate', 'expert']);

	return {
		selectedMuscle,
		setSelectedMuscle,
		selectedDifficulties,
		setSelectedDifficulties,
	};
};

export default useExerciseFilters;
