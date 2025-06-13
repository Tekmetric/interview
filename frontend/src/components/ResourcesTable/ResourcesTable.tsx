import { useIntl } from 'react-intl';
import { bindActionCreators, type Dispatch } from '@reduxjs/toolkit';
import { connect } from 'react-redux';
import { useMemo } from 'react';

import {
	resourcesActions,
	type ResourcesStateInterface,
} from '_store/slices/resources';
import { type RootState } from '_store/index';

import DataGrid from './DataGrid';
import { HEADERS } from './constants';
import cellContentRenderer from './Cells/CellContentRenderer';

interface ResourcesTableInterface {
	items: ResourcesStateInterface['items'];
	getResources: (typeof resourcesActions)['getResources'];
	canQueryMore: ResourcesStateInterface['paginationConfig']['canQueryMore'];
	hasFetchError: ResourcesStateInterface['hasFetchError'];
	isLoading: ResourcesStateInterface['isLoading'];
	onSelectResource: (typeof resourcesActions)['selectResource'];
	onSelectAllResources: (typeof resourcesActions)['selectAllResources'];
	isAllSelected: ResourcesStateInterface['isAllSelected'];
	numberOfSelectedItems: ResourcesStateInterface['numberOfSelectedItems'];
	isSelectedMap: ResourcesStateInterface['isSelectedMap'];
	onSort: (typeof resourcesActions)['sort'];
	sortConfig: ResourcesStateInterface['sortConfig'];
}

function ResourcesTable({
	items,
	getResources,
	canQueryMore,
	hasFetchError,
	isLoading,
	onSelectResource,
	onSelectAllResources,
	isAllSelected,
	numberOfSelectedItems,
	isSelectedMap,
	onSort,
	sortConfig,
}: ResourcesTableInterface) {
	const { formatMessage } = useIntl();

	const translatedHeaders = useMemo(
		() =>
			HEADERS.map((header) => ({
				...header,
				label: header.label && formatMessage({ id: header.label }),
			})),
		[formatMessage]
	);

	return (
		<DataGrid
			headers={translatedHeaders}
			data={items}
			getItems={getResources}
			isLoading={isLoading}
			hasFetchError={hasFetchError}
			canQueryMore={canQueryMore}
			cellContentRenderer={cellContentRenderer}
			onSelectItem={onSelectResource}
			onSelectAll={onSelectAllResources}
			isSelectedMap={isSelectedMap}
			isAllSelected={isAllSelected}
			numberOfSelectedItems={numberOfSelectedItems}
			onSort={onSort}
			sortConfig={sortConfig}
			noDataAvailableLabel={formatMessage({
				id: 'NO_RESOURCES_AVAILABLE',
			})}
			fetchErrorLabel={formatMessage({
				id: 'FAILED_TO_GET_RESOURCES',
			})}
			loadingLabel={formatMessage({
				id: 'LOADING',
			})}
		/>
	);
}

const mapStateToProps = (state: RootState) => ({
	items: state.resources.items,
	canQueryMore: state.resources.paginationConfig.canQueryMore,
	hasFetchError: state.resources.hasFetchError,
	isLoading: state.resources.isLoading,
	isAllSelected: state.resources.isAllSelected,
	numberOfSelectedItems: state.resources.numberOfSelectedItems,
	isSelectedMap: state.resources.isSelectedMap,
	sortConfig: state.resources.sortConfig,
});

const mapDispatchToProps = (dispatch: Dispatch) => ({
	getResources: bindActionCreators(resourcesActions.getResources, dispatch),
	onSelectResource: bindActionCreators(
		resourcesActions.selectResource,
		dispatch
	),
	onSelectAllResources: bindActionCreators(
		resourcesActions.selectAllResources,
		dispatch
	),
	onSort: bindActionCreators(resourcesActions.sort, dispatch),
});

export default connect(mapStateToProps, mapDispatchToProps)(ResourcesTable);
