/* eslint-disable sonarjs/pseudo-random */
import { faker } from '@faker-js/faker';
import { v4 as uuid } from 'uuid';

export interface ResourceInterface {
	id: string;
	title: string;
	type: 'book' | 'video' | 'article' | 'repository' | 'course';
	link: string;
	description?: string;
	tags?: string[];
	createdAt: string;
}

const types = ['book', 'video', 'article', 'repository', 'course'] as const;

function generateData(count: number): ResourceInterface[] {
	return Array.from({ length: count }, () => ({
		id: uuid(),
		title: faker.lorem.words(3).replace(/^\w/, (c) => c.toUpperCase()),
		type: types[
			Math.floor(Math.random() * types.length)
		] as (typeof types)[number],
		link: faker.internet.url(),
		description: faker.lorem.sentence(),
		tags: Array.from({ length: Math.floor(Math.random() * 5) }, () =>
			faker.company.buzzNoun()
		),
		createdAt: faker.date.past().toISOString(),
	}));
}

export default generateData;
