import { type ResourceInterface } from './data';

function sleep(timeout: number) {
	return new Promise((resolve) => {
		setTimeout(() => {
			resolve(true);
		}, timeout);
	});
}

const SORTABLE_COLUMNS = [
	'title',
	'type',
	'link',
	'description',
	'createdAt',
] satisfies (keyof ResourceInterface)[];

function getSortedList(
	data: ResourceInterface[],
	sortColumn: string,
	sortDirection: 'asc' | 'desc'
) {
	if (!sortColumn || !SORTABLE_COLUMNS.includes(sortColumn as any)) {
		return data;
	}

	return data.toSorted((a, b) => {
		const aValue = a[sortColumn as keyof ResourceInterface];
		const bValue = b[sortColumn as keyof ResourceInterface];

		if (sortColumn === 'createdAt') {
			const aDate = new Date(aValue as string);
			const bDate = new Date(bValue as string);

			return sortDirection === 'asc'
				? aDate.getTime() - bDate.getTime()
				: bDate.getTime() - aDate.getTime();
		}

		if (typeof aValue === 'string' && typeof bValue === 'string') {
			return sortDirection === 'asc'
				? aValue.localeCompare(bValue)
				: bValue.localeCompare(aValue);
		}

		return 0;
	});
}

export { sleep, getSortedList };
