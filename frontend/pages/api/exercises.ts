import { NextApiRequest, NextApiResponse } from 'next';
import axios from 'axios';

export default async function handler(
	req: NextApiRequest,
	res: NextApiResponse
) {
	if (req.method === 'GET') {
		const muscle = req.query.muscle;

		if (!muscle) {
			return res.status(400).json({ message: 'Muscle parameter is required' });
		}

		try {
			const response = await axios.get(
				'https://api.api-ninjas.com/v1/exercises',
				{
					headers: {
						'X-Api-Key': process.env.REACT_APP_NINJAS_API_KEY || '',
					},
					params: {
						muscle,
					},
				}
			);

			res.status(200).json(response.data);
		} catch (error: any) {
			res
				.status(error.response?.status || 500)
				.json({ message: error.message });
		}
	} else {
		res.setHeader('Allow', ['GET']);

		res.status(405).end(`Method ${req.method} Not Allowed`);
	}
}
