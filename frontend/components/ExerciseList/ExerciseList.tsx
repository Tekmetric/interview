import React, { FC, useMemo } from 'react';
import { useQuery } from '@tanstack/react-query';
import { fetchExercises } from './data/fetch-exercises/fetch-exercises';
import { Exercise } from '../Exercise/Exercise';
import {
	ExerciseMuscle,
	Exercise as ExerciseType,
	ExerciseDifficulty,
} from '../../types';

interface Props {
	muscle: ExerciseMuscle;
	selectedDifficulties: ExerciseDifficulty[];
}

export const ExerciseList: FC<Props> = ({ muscle, selectedDifficulties }) => {
	const { data, isLoading, isError, error } = useQuery<ExerciseType[], Error>({
		queryKey: ['exercises', muscle],
		queryFn: () => fetchExercises(muscle),
		retry: 2,
		staleTime: 1000 * 60 * 5, // Cache 5 minutes
	});

	const filteredExercises = useMemo(() => {
		return (
			data?.filter((exercise) =>
				selectedDifficulties.includes(exercise.difficulty)
			) || []
		);
	}, [data, selectedDifficulties]);

	if (isLoading) {
		return (
			<div className='flex justify-center items-center'>
				<span className='loading loading-dots loading-lg'>
					Loading exercises...
				</span>
			</div>
		);
	}

	if (isError) {
		return (
			<div role='alert' className='alert alert-error'>
				<span>
					Error loading exercises:{' '}
					{error?.message || 'An unknown error occurred.'}
				</span>
			</div>
		);
	}

	if (!data || filteredExercises.length === 0) {
		return (
			<div className='text-center text-gray-500'>
				<p>No exercises found for the selected difficulty levels.</p>
			</div>
		);
	}

	if (!data) {
		return null;
	}

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
