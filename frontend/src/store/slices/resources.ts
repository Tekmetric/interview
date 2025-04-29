import { createAsyncThunk, createSlice, type Reducer } from '@reduxjs/toolkit';

import {
	getResources as apiGetResources,
	deleteResource as apiDeleteResource,
	bulkDeleteResources as apiBulkDeleteResources,
} from '../../service/api/resources';

export interface ResourceInterface {
	id: string;
	title: string;
	type: 'book' | 'video' | 'article' | 'repository' | 'course';
	link: string;
	description?: string;
	tags?: string[];
	createdAt: string;
}

export interface ResourcesStateInterface {
	items: ResourceInterface[];
	searchText: string;
	paginationConfig: {
		offset: number;
		limit: number;
		canQueryMore: boolean;
	};
	sortConfig: {
		column?: string;
		order: 'asc' | 'desc';
	};
	hasFetchError: boolean;
	isLoading: boolean;
	isDeleteLoading: boolean;
	isSelectedMap: Record<string, boolean>;
	numberOfSelectedItems: number;
	isAllSelected: boolean;
	requestIds: {
		getResources?: string;
	};
}

const initialState: ResourcesStateInterface = {
	items: [],
	searchText: '',
	paginationConfig: {
		offset: 0,
		limit: 10,
		canQueryMore: true,
	},
	sortConfig: {
		column: undefined,
		order: 'asc',
	},
	hasFetchError: false,
	isLoading: false,
	isDeleteLoading: false,
	isSelectedMap: {},
	numberOfSelectedItems: 0,
	isAllSelected: false,
	requestIds: {
		getResources: undefined,
	},
};

function partialResetState(state: ResourcesStateInterface) {
	state.paginationConfig.offset = 0;
	state.paginationConfig.canQueryMore = true;
	state.items = [];
	state.isSelectedMap = {};
	state.numberOfSelectedItems = 0;
	state.isAllSelected = false;
	state.isLoading = false;
	state.hasFetchError = false;
}

const getResources = createAsyncThunk(
	'resources/getResources',
	async (payload, { getState }: { getState: any }) => {
		const { searchText, paginationConfig, sortConfig } = getState()
			.resources as ResourcesStateInterface;

		const response = await apiGetResources({
			filter: searchText,
			limit: paginationConfig.limit,
			offset: paginationConfig.offset,
			sortDirection: sortConfig.order,
			sortColumn: sortConfig.column,
		});

		return response.data;
	}
);

const deleteResource = createAsyncThunk(
	'resources/deleteResource',
	async (payload: { id: string }) => {
		const response = await apiDeleteResource(payload.id);

		return response.data;
	}
);

const bulkDeleteResources = createAsyncThunk(
	'resources/bulkDeleteResources',
	async (payload, { getState }: { getState: any }) => {
		const { isSelectedMap } = getState().resources as ResourcesStateInterface;

		const idsList = Object.keys(isSelectedMap).filter(
			(key) => isSelectedMap[key] === true
		);
		const response = await apiBulkDeleteResources(idsList);

		return response.data;
	}
);

const resources = createSlice({
	name: 'resources',
	initialState,
	reducers: {
		refreshData: (state) => {
			partialResetState(state);
		},
		setSearchText: (state, { payload }: { payload: string }) => {
			state.searchText = payload;

			partialResetState(state);
		},
		selectResource: (state, { payload }: { payload: string }) => {
			if (state.isSelectedMap[payload]) {
				state.isSelectedMap[payload] = false;
				state.numberOfSelectedItems -= 1;
			} else {
				state.isSelectedMap[payload] = true;
				state.numberOfSelectedItems += 1;
			}

			state.isAllSelected =
				state.numberOfSelectedItems > 0 &&
				state.numberOfSelectedItems === state.items?.length
					? true
					: false;
		},
		selectAllResources: (state) => {
			if (state.isAllSelected) {
				state.isAllSelected = false;
				state.numberOfSelectedItems = 0;
				state.isSelectedMap = {};
			} else {
				state.isAllSelected = true;
				state.numberOfSelectedItems = state.items?.length || 0;
				const newSelectionMap: Record<string, boolean> = {};

				state.items?.forEach((resource) => {
					newSelectionMap[resource.id] = true;
				});

				state.isSelectedMap = newSelectionMap;
			}
		},
		sort: (
			state,
			{
				payload,
			}: {
				payload: string;
			}
		) => {
			if (state.sortConfig.column === payload) {
				const newSortDirection =
					state.sortConfig.order === 'asc' ? 'desc' : 'asc';

				state.sortConfig.order = newSortDirection;
			} else {
				state.sortConfig.order = 'asc';
				state.sortConfig.column = payload;
			}

			partialResetState(state);
		},
	},
	extraReducers: (builder) => {
		builder
			.addCase(getResources.pending, (state, { meta }) => {
				state.isLoading = true;
				state.hasFetchError = false;
				state.requestIds.getResources = meta.requestId;
			})
			.addCase(getResources.fulfilled, (state, { payload, meta }) => {
				if (state.requestIds.getResources !== meta.requestId) {
					return;
				}

				state.isLoading = false;
				const newItems = [...(state.items ?? []), ...payload.data];
				const newItemsMap: Record<string, boolean> = {};

				state.items = newItems.filter((item) => {
					if (newItemsMap[item.id]) {
						return false;
					}

					newItemsMap[item.id] = true;

					return true;
				});

				state.paginationConfig.canQueryMore = payload.canQueryMore;
				state.paginationConfig.offset =
					state.paginationConfig.offset + payload.data.length;
				state.requestIds.getResources = undefined;
				state.isAllSelected = false;
			})
			.addCase(getResources.rejected, (state) => {
				state.isLoading = false;
				state.hasFetchError = true;
			})
			.addCase(deleteResource.pending, (state) => {
				state.isDeleteLoading = true;
			})
			.addCase(deleteResource.fulfilled, (state, { payload }) => {
				state.isDeleteLoading = false;
				const newItems = state.items.filter(
					(item) => item.id !== payload.deletedId
				);

				state.items = newItems;

				if (state.isSelectedMap[payload.id]) {
					state.isSelectedMap[payload.id] = false;
					state.numberOfSelectedItems -= 1;
				}

				state.isAllSelected =
					state.numberOfSelectedItems >= 0 &&
					state.items.length === state.numberOfSelectedItems
						? true
						: false;
				state.paginationConfig.offset -= 1;
			})
			.addCase(deleteResource.rejected, (state) => {
				state.isDeleteLoading = false;
			})
			.addCase(bulkDeleteResources.pending, (state) => {
				state.isDeleteLoading = true;
			})
			.addCase(bulkDeleteResources.fulfilled, (state, { payload }) => {
				state.isDeleteLoading = false;

				const newItems = state.items.filter(
					(item) => !payload.deletedIds.includes(item.id)
				);

				state.items = newItems;

				state.isSelectedMap = {};
				state.numberOfSelectedItems = 0;
				state.isAllSelected = false;
				state.paginationConfig.offset -= payload.deletedIds.length;
			})
			.addCase(bulkDeleteResources.rejected, (state) => {
				state.isDeleteLoading = false;
			});
	},
});

const resourcesActions = {
	...resources.actions,
	getResources,
	deleteResource,
	bulkDeleteResources,
};

export { getResources, resourcesActions };
export default resources.reducer as Reducer<ResourcesStateInterface>;
