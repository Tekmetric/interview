import { useEffect, useMemo, useRef } from 'react';

import { type DataGridInterface } from './utils/types';
import computeGridColumnsTemplate from './utils/computeGridColumnsTemplate';
import {
	Cell,
	Checkbox,
	Container,
	CustomCell,
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
	const previousContainerHeight = useRef<number>(0);

	// the function passed in the ResizeObserver is "memoized", so we need to use a ref to store the params
	// otherwise, we have 2 options - fire the useEffect at each change and rebuild the resizer at each change
	// 															- or we fire the function with stale params and hope for the best
	const onScrollParamsAsRef = useRef({
		dataLength: data.length,
		isLoading,
		hasFetchError,
		canQueryMore,
		rowHeight,
	});

	onScrollParamsAsRef.current = {
		dataLength: data.length,
		isLoading,
		hasFetchError,
		canQueryMore,
		rowHeight,
	};

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

	useEffect(() => {
		if (!containerRef.current) {
			return;
		}

		const observer = new ResizeObserver((entries) => {
			for (const entry of entries) {
				if (
					entry.contentRect.height - previousContainerHeight.current >
					(TOLERANCE_ROWS_COUNT * rowHeight) / 2
				) {
					onScroll();
					previousContainerHeight.current = entry.contentRect.height;
				}
			}
		});

		observer.observe(containerRef.current);

		return () => {
			if (containerRef.current) {
				observer.unobserve(containerRef.current);
			}

			observer.disconnect();
		};
	}, []);

	function onScroll() {
		const {
			isLoading: localIsLoading,
			canQueryMore: localCanQueryMore,
			hasFetchError: localHasFetchError,
			rowHeight: localRowHeight,
			dataLength: localDataLength,
		} = onScrollParamsAsRef.current;

		if (!localIsLoading && localCanQueryMore && !localHasFetchError) {
			const { scrollTop, clientHeight } = scrollableContentRef.current || {
				scrollTop: 0,
				clientHeight: 0,
			};

			if (
				(localDataLength || 0) * localRowHeight - scrollTop - clientHeight <
				localRowHeight * TOLERANCE_ROWS_COUNT
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
					<CustomCell height={data?.length ? rowHeight : undefined}>
						<NoDataDisplayContainer>{loadingLabel}</NoDataDisplayContainer>
					</CustomCell>
				)}

				{!data?.length && !isLoading && (
					<CustomCell>
						<NoDataDisplayContainer>
							{hasFetchError ? fetchErrorLabel : noDataAvailableLabel}
						</NoDataDisplayContainer>
					</CustomCell>
				)}
			</ScrollableContent>
		</Container>
	);
}

export default DataGrid;
