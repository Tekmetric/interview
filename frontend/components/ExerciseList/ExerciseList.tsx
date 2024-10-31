import React, { FC } from 'react';
import { useQuery } from '@tanstack/react-query';
import {
	fetchExercises,
	Exercise,
} from './data/fetch-exercises/fetch-exercises';

interface Props {
	muscle: string;
}

export const ExerciseList: FC<Props> = ({ muscle }) => {
	const { data, isLoading, error } = useQuery<Exercise[], Error>({
		queryKey: ['exercises', muscle],
		queryFn: () => fetchExercises(muscle),
	});

	if (isLoading) {
		return <div>Loading...</div>;
	}

	if (error) {
		return <div>Error loading exercises</div>;
	}

	if (!data) {
		return null;
	}

	return (
		<div>
			<h2>Exercise List</h2>

			<ul>
				{data.map((exercise: Exercise) => (
					<li key={exercise.name}>
						<strong>{exercise.name}</strong> - {exercise.type} -{' '}
						{exercise.muscle} - Difficulty: {exercise.difficulty}
						<br />
						Equipment: {exercise.equipment}
						<br />
						Instructions: {exercise.instructions}
					</li>
				))}
			</ul>
		</div>
	);
};

export default ExerciseList;
