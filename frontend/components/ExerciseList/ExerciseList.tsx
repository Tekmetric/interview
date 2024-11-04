import React, { FC } from 'react';
import { useQuery } from '@tanstack/react-query';
import { fetchExercises } from './data/fetch-exercises/fetch-exercises';
import { Exercise } from '../Exercise/Exercise';
import {
	ExerciseMuscle,
	Exercise as ExerciseType,
	ExerciseDifficulty,
} from '../types';

interface Props {
	muscle: ExerciseMuscle;
	selectedDifficulties: ExerciseDifficulty[];
}

export const ExerciseList: FC<Props> = ({ muscle, selectedDifficulties }) => {
	const { data, isLoading, error } = useQuery<ExerciseType[], Error>({
		queryKey: ['exercises', muscle],
		queryFn: () => fetchExercises(muscle),
	});

	if (isLoading) {
		return <span className='loading loading-dots loading-lg' />;
	}

	if (error) {
		return <div>Error loading exercises</div>;
	}

	if (!data) {
		return null;
	}

	const filteredExercises = data.filter((exercise) =>
		selectedDifficulties.includes(exercise.difficulty)
	);

	return (
		<div>
			<ul className='flex flex-col gap-3'>
				{filteredExercises.map((exercise: ExerciseType) => (
					<Exercise key={exercise.name} {...exercise} />
				))}
			</ul>
		</div>
	);
};

export default ExerciseList;
