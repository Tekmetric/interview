import { useEffect, useMemo, useRef } from 'react';

import { type DataGridInterface } from './types';
import computeGridColumnsTemplate from './utils/computeGridColumnsTemplate';
import {
	Cell,
	Checkbox,
	Container,
	CustomCell,
	CustomRow,
	HeaderRow,
	NoDataDisplayContainer,
	Row,
	ScrollableContent,
} from './DataGrid.styled';
import HeaderCell from './HeaderCell/HeaderCell';
import { StyledHeaderCell } from './HeaderCell/HeaderCell.styled';

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
	noDataAvailableLabel = 'No data available',
	fetchErrorLabel = 'Error fetching data',
	loadingLabel = 'Loading...',
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
					<StyledHeaderCell>
						<Checkbox
							ref={checkboxRef}
							type="checkbox"
							onChange={() => {
								onSelectAll();
							}}
							checked={isAllSelected}
						/>
					</StyledHeaderCell>

					{headers.map((header) => (
						<HeaderCell
							key={header.key}
							header={header}
							onSort={onSort}
							sortConfig={sortConfig}
						/>
					))}
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

				{isLoading && (
					<CustomRow
						template={gridColumnsTemplate}
						height={data?.length ? rowHeight : undefined}
					>
						<CustomCell>
							<NoDataDisplayContainer>{loadingLabel}</NoDataDisplayContainer>
						</CustomCell>
					</CustomRow>
				)}

				{!data?.length && !isLoading && (
					<CustomRow template={gridColumnsTemplate}>
						<CustomCell>
							<NoDataDisplayContainer>
								{hasFetchError ? fetchErrorLabel : noDataAvailableLabel}
							</NoDataDisplayContainer>
						</CustomCell>
					</CustomRow>
				)}
			</ScrollableContent>
		</Container>
	);
}

export default DataGrid;
