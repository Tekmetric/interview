import React from 'react';

interface MuscleSelectProps {
	selectedMuscle: string;
	setSelectedMuscle: (muscle: string) => void;
}

const MuscleGroupSelect: React.FC<MuscleSelectProps> = ({
	selectedMuscle,
	setSelectedMuscle,
}) => {
	const muscles = [
		'abdominals',
		'abductors',
		'adductors',
		'biceps',
		'calves',
		'chest',
		'forearms',
		'glutes',
		'hamstrings',
		'lats',
		'lower_back',
		'middle_back',
		'neck',
		'quadriceps',
		'traps',
		'triceps',
	];

	return (
		<div className='mb-4'>
			<label htmlFor='muscle-select' className='block text-lg font-medium mb-2'>
				Select Muscle Group
			</label>
			<select
				id='muscle-select'
				value={selectedMuscle}
				onChange={(e) => setSelectedMuscle(e.target.value)}
				className='select select-bordered w-full max-w-xs'
			>
				<option value=''>--Please choose an option--</option>
				{muscles.map((muscle) => (
					<option key={muscle} value={muscle}>
						{muscle}
					</option>
				))}
			</select>
		</div>
	);
};

export default MuscleGroupSelect;
