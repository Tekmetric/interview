import { ExerciseDifficulty, ExerciseType } from '../types';

export const ExerciseBadges = ({
	type,
	difficulty,
}: {
	type: ExerciseType;
	difficulty: ExerciseDifficulty;
}) => {
	const getBadgeVariant = (difficulty: ExerciseDifficulty): string => {
		switch (difficulty) {
			case 'beginner':
				return 'badge-success';
			case 'intermediate':
				return 'badge-warning';
			case 'expert':
				return 'badge-error';
			default:
				return 'badge-primary';
		}
	};

	return (
		<div className='flex gap-4'>
			<div className='badge badge-primary w-32'>{type}</div>

			<div className={`badge ${getBadgeVariant(difficulty)} w-32`}>
				{difficulty}
			</div>
		</div>
	);
};
