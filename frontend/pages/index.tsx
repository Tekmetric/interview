import React, { FC, useState } from 'react';
import ExerciseList from '../components/ExerciseList/ExerciseList';
import MuscleSelect from '../components/MuscleGroupSelect/MuscleGroupSelect';

const Home: FC = () => {
	const [selectedMuscle, setSelectedMuscle] = useState<string>('');

	return (
		<div className='Home'>
			<h1>Fitness App</h1>

			<MuscleSelect
				selectedMuscle={selectedMuscle}
				setSelectedMuscle={setSelectedMuscle}
			/>

			{selectedMuscle && <ExerciseList muscle={selectedMuscle} />}
		</div>
	);
};

export default Home;
