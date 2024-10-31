import React, { FC, useState } from 'react';
import ExerciseList from '../components/ExerciseList/ExerciseList';
import MuscleSelect from '../components/MuscleGroupSelect/MuscleGroupSelect';

const Home: FC = () => {
	const [selectedMuscle, setSelectedMuscle] = useState<string>('');

	return (
		<div>
			<div>
				<h1>Fitness Tracker</h1>
				<MuscleSelect
					selectedMuscle={selectedMuscle}
					setSelectedMuscle={setSelectedMuscle}
				/>
				{selectedMuscle && <ExerciseList muscle={selectedMuscle} />}
			</div>
		</div>
	);
};

export default Home;
