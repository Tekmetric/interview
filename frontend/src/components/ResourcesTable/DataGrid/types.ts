interface DataGridHeaderInterface<T = string> {
	label: string;
	key: T;
	isSortable?: boolean;
	width: string;
	minWidth?: string;
}

interface DataGridInterface<T extends { id: string } = { id: string }> {
	headers: DataGridHeaderInterface[];
	data: T[] | undefined;
	rowHeight?: number;
	getItems: () => void;
	isLoading: boolean;
	hasFetchError: boolean;
	canQueryMore: boolean;
	cellContentRenderer: ({
		item,
		key,
	}: {
		item: T;
		key: string;
	}) => React.ReactNode;
	onSelectItem: (itemId: string) => void;
	onSelectAll: () => void;
	isSelectedMap: Record<string, boolean>;
	isAllSelected: boolean;
	numberOfSelectedItems: number;
	onSort: (key: string) => void;
	sortConfig: { column?: string; order: 'asc' | 'desc' };
}

export { DataGridHeaderInterface, DataGridInterface };
