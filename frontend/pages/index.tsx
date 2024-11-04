import React, { FC, useState } from 'react';
import ExerciseList from '../components/ExerciseList/ExerciseList';
import { MuscleGroupSelect } from '../components/MuscleGroupSelect/MuscleGroupSelect';
import { ExerciseDifficulty, ExerciseMuscle } from '../components/types';
import { DifficultyFilter } from '../components/DifficultyFilter/DifficultyFilter';
import { Logo } from '../components/Logo/Logo';

const Home: FC = () => {
	const [selectedMuscle, setSelectedMuscle] = useState<ExerciseMuscle | ''>('');
	const [selectedDifficulties, setSelectedDifficulties] = useState<
		ExerciseDifficulty[]
	>(['beginner', 'intermediate', 'expert']);

	return (
		<div className='flex flex-col justify-center items-center'>
			<Logo />

			<div className='flex flex-col gap-6 w-full max-w-4xl mt-20 px-8'>
				<div className='flex gap-20 justify-between'>
					<MuscleGroupSelect
						selectedMuscle={selectedMuscle}
						setSelectedMuscle={setSelectedMuscle}
					/>

					<DifficultyFilter
						selectedDifficulties={selectedDifficulties}
						setSelectedDifficulties={setSelectedDifficulties}
					/>
				</div>

				<div className='mt-10'>
					{selectedMuscle && selectedDifficulties.length > 0 && (
						<ExerciseList
							muscle={selectedMuscle}
							selectedDifficulties={selectedDifficulties}
						/>
					)}
				</div>
			</div>
		</div>
	);
};

export default Home;
