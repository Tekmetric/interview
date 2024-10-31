import React from 'react';

interface MuscleSelectProps {
	selectedMuscle: string;
	setSelectedMuscle: (muscle: string) => void;
}

const MuscleSelect: React.FC<MuscleSelectProps> = ({
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
		<div>
			<label htmlFor='muscle-select'>Select Muscle Group: </label>
			<select
				id='muscle-select'
				value={selectedMuscle}
				onChange={(e) => setSelectedMuscle(e.target.value)}
			>
				<option value=''>Select a muscle group</option>

				{muscles.map((muscle) => (
					<option key={muscle} value={muscle}>
						{muscle}
					</option>
				))}
			</select>
		</div>
	);
};

export default MuscleSelect;
