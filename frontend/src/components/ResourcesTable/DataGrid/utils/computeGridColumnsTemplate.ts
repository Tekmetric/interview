import { type DataGridHeaderInterface } from './types';

function computeGridColumnsTemplate<T extends string>(
	headers: DataGridHeaderInterface<T>[]
): string {
	return (
		'40px ' +
		headers
			.map((header) => {
				const { width, minWidth } = header;

				return `minmax(${minWidth ?? width}, ${width})`;
			})
			.join(' ')
	);
}

export default computeGridColumnsTemplate;
