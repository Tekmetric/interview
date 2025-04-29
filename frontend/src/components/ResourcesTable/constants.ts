import { type ResourceInterface } from '../../store/slices/resources';
import { type DataGridHeaderInterface } from './DataGrid';

const LARGE_CELL_WIDTH = '30%';
const LARGE_CELL_MIN_WIDTH = '300px';
const MEDIUM_CELL_WIDTH = '25%';
const MEDIUM_CELL_MIN_WIDTH = '250px';
const SMALL_CELL_WIDTH = '20%';
const SMALL_CELL_MIN_WIDTH = '200px';

const HEADERS = [
	{
		label: 'TITLE',
		key: 'title',
		isSortable: true,
		width: LARGE_CELL_WIDTH,
		minWidth: LARGE_CELL_MIN_WIDTH,
	},
	{
		label: 'TYPE',
		key: 'type',
		isSortable: true,
		width: SMALL_CELL_WIDTH,
		minWidth: SMALL_CELL_MIN_WIDTH,
	},
	{
		label: 'LINK',
		key: 'link',
		isSortable: false,
		width: LARGE_CELL_WIDTH,
		minWidth: LARGE_CELL_MIN_WIDTH,
	},
	{
		label: 'DESCRIPTION',
		key: 'description',
		isSortable: false,
		width: LARGE_CELL_WIDTH,
		minWidth: LARGE_CELL_MIN_WIDTH,
	},
	{
		label: 'TAGS',
		key: 'tags',
		isSortable: false,
		width: LARGE_CELL_WIDTH,
		minWidth: LARGE_CELL_MIN_WIDTH,
	},
	{
		label: 'CREATED_AT',
		key: 'createdAt',
		isSortable: true,
		width: MEDIUM_CELL_WIDTH,
		minWidth: MEDIUM_CELL_MIN_WIDTH,
	},
] as const satisfies DataGridHeaderInterface<keyof ResourceInterface>[];

type HeaderKeys = (typeof HEADERS)[number]['key'];

export { HEADERS };
export type { HeaderKeys };
