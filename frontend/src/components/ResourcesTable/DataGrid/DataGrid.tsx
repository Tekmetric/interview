import { useEffect, useMemo, useRef } from 'react';

import { type DataGridInterface } from './types';
import computeGridColumnsTemplate from './utils/computeGridColumnsTemplate';
import {
	Cell,
	Checkbox,
	Container,
	HeaderActiveCell,
	HeaderCell,
	HeaderRow,
	Row,
	ScrollableContent,
} from './DataGrid.styled';

const TOLERANCE_ROWS_COUNT = 5;

function DataGrid<T extends { id: string }>({
	headers,
	data,
	rowHeight = 40,
	getItems,
	isLoading,
	hasFetchError,
	canQueryMore,
	cellContentRenderer,
	onSelectItem,
	onSelectAll,
	isSelectedMap,
	isAllSelected,
	numberOfSelectedItems,
	onSort,
	sortConfig,
}: DataGridInterface<T>) {
	const containerRef = useRef<HTMLDivElement>(null);
	const scrollableContentRef = useRef<HTMLDivElement>(null);
	const checkboxRef = useRef<HTMLInputElement>(null);

	const gridColumnsTemplate = useMemo(
		() => computeGridColumnsTemplate(headers),
		[]
	);

	useEffect(() => {
		if (checkboxRef.current) {
			checkboxRef.current.indeterminate =
				numberOfSelectedItems > 0 && !isAllSelected;
		}
	}, [numberOfSelectedItems, isAllSelected]);

	useEffect(() => {
		if (!data?.length) {
			scrollableContentRef.current?.scrollTo({
				top: 0,
				left: 0,
			});
		}
	}, [data]);

	useEffect(() => {
		onScroll();
	}, [data?.length, isLoading, hasFetchError, canQueryMore, rowHeight]);

	function onScroll() {
		if (!isLoading && canQueryMore && !hasFetchError) {
			const { scrollTop, clientHeight } = scrollableContentRef.current || {
				scrollTop: 0,
				clientHeight: 0,
			};

			if (
				(data?.length || 0) * rowHeight - scrollTop - clientHeight <
				rowHeight * TOLERANCE_ROWS_COUNT
			) {
				getItems();
			}
		}
	}

	return (
		<Container ref={containerRef}>
			<ScrollableContent onScroll={onScroll} ref={scrollableContentRef}>
				<HeaderRow template={gridColumnsTemplate}>
					<HeaderCell>
						<Checkbox
							ref={checkboxRef}
							type="checkbox"
							onChange={() => {
								onSelectAll();
							}}
							checked={isAllSelected}
						/>
					</HeaderCell>

					{headers.map((header) => {
						if (header.isSortable) {
							return (
								<HeaderActiveCell
									key={header.key}
									onClick={() => {
										onSort(header.key);
									}}
								>
									{header.label}
								</HeaderActiveCell>
							);
						}

						return <HeaderCell key={header.key}>{header.label}</HeaderCell>;
					})}
				</HeaderRow>

				{data?.map((item) => (
					<Row key={item.id} template={gridColumnsTemplate}>
						<Cell height={rowHeight}>
							<Checkbox
								type="checkbox"
								onChange={() => {
									onSelectItem(item.id);
								}}
								checked={isSelectedMap[item.id] ?? false}
								onClick={(event) => {
									event.stopPropagation();
								}}
							/>
						</Cell>

						{headers.map((header) => (
							<Cell key={header.key} height={rowHeight}>
								{cellContentRenderer({
									item,
									key: header.key,
								})}
							</Cell>
						))}
					</Row>
				))}
			</ScrollableContent>
		</Container>
	);
}

export default DataGrid;
