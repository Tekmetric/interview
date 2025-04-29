import express, { type Request, type Response } from 'express';
import cors from 'cors';
import { v4 as uuid } from 'uuid';

import generateData, { type ResourceInterface } from './data';
import { getSortedList, sleep } from './utils';

const app = express();

app.disable('x-powered-by');

const corsOptions = {
	origin: ['http://localhost:3000'],
	methods: ['GET', 'POST', 'PUT', 'DELETE'],
};

app.use(cors(corsOptions));
app.use(express.json());
app.use(express.json());

app.set('port', process.env.PORT || 8000);

let database = generateData(500);
const DELAY = 1000;

app.get('/api/resources', async (request: Request, response: Response) => {
	await sleep(DELAY);

	const {
		filter,
		limit = 50,
		offset = 0,
		sortDirection,
		sortColumn,
	} = request.query;

	const filteredResources = database.filter(
		(item) =>
			!filter ||
			item.title.toLowerCase().includes((filter as string)?.toLowerCase())
	);

	const sortedResources = getSortedList(
		filteredResources,
		sortColumn as string,
		sortDirection as 'asc' | 'desc'
	);

	if (sortedResources.length === 0) {
		response.status(200).json({
			data: [],
			canQueryMore: false,
		});

		return;
	}

	const paginatedResources = sortedResources.slice(
		Number(offset),
		Number(offset) + Number(limit)
	);

	response.status(200).json({
		data: paginatedResources,
		canQueryMore: sortedResources.length > Number(offset) + Number(limit),
	});
});

app.post('/api/resources', async (request: Request, response: Response) => {
	await sleep(DELAY);

	const { resource } = request.body;

	if (!resource) {
		response.status(400).json({ message: 'Invalid request' });

		return;
	}

	if (!resource.title || !resource.type || !resource.link) {
		response.status(400).json({ message: 'Invalid request' });

		return;
	}

	const newResource: ResourceInterface = {
		...resource,
		id: uuid(),
		createdAt: new Date().toISOString(),
	};

	database.push(newResource);

	response.status(201).json(newResource);
});

app.put('/api/resources', async (request: Request, response: Response) => {
	await sleep(DELAY);

	const { resource } = request.body;

	const { id, filteredResourceFields } = resource;

	if (!resource) {
		response.status(400).json({ message: 'Invalid request' });

		return;
	}

	const index = database.findIndex((item) => item.id === id);

	if (index === -1) {
		response.status(404).json({ message: 'Resource not found' });

		return;
	}

	database[index] = { ...database[index], ...filteredResourceFields };
	response.status(200).json(database[index]);
});

app.delete(
	'/api/resources/one/:id',
	async (request: Request, response: Response) => {
		await sleep(DELAY);

		const { id } = request.params;
		const index = database.findIndex((item) => item.id === id);

		if (index === -1) {
			response.status(404).json({ message: 'Resource not found' });

			return;
		}

		database.splice(index, 1);
		response.status(200).json({ deletedId: id });
	}
);

app.delete(
	'/api/resources/bulk',
	async (request: Request, response: Response) => {
		await sleep(2 * DELAY);

		const { ids } = request.body;

		if (!Array.isArray(ids)) {
			response.status(400).json({ message: 'Invalid request' });

			return;
		}

		const deletedIds: string[] = [];

		database = database.filter((item) => {
			if (ids.includes(item.id)) {
				deletedIds.push(item.id);

				return false;
			}

			return true;
		});

		response.status(200).json({ deletedIds });
	}
);

app.get('*', (request: Request, response: Response) => {
	response.status(404).json({ message: 'Not found' });
});

app.listen(app.get('port'), () => {
	// eslint-disable-next-line no-console
	console.log(`Server is running on http://localhost:${app.get('port')}`);
});
