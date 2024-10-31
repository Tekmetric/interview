import axios from 'axios';

export interface Exercise {
	name: string;
	type: string;
	muscle: string;
	equipment: string;
	difficulty: string;
	instructions: string;
}

export const fetchExercises = async (muscle: string): Promise<Exercise[]> => {
	const { data } = await axios.get<Exercise[]>('/api/exercises', {
		params: { muscle },
	});

	return data;
};
