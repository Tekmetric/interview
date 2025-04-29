import { createAsyncThunk, createSlice, type Reducer } from '@reduxjs/toolkit';

import { getResources as apiGetResources } from '../../service/api/resources';

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
	resources: ResourceInterface[];
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
	isSelectedMap: Record<string, boolean>;
	numberOfSelectedItems: number;
	isAllSelected: boolean;
	requestIds: {
		getResources?: string;
	};
}

const initialState: ResourcesStateInterface = {
	resources: [],
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
	state.resources = [];
	state.isSelectedMap = {};
	state.numberOfSelectedItems = 0;
	state.isAllSelected = false;
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

const resources = createSlice({
	name: 'resources',
	initialState,
	reducers: {
		setSearchText: (state, { payload }: { payload: string }) => {
			state.searchText = payload;

			//reset state
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
				state.numberOfSelectedItems === state.resources?.length
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
				state.numberOfSelectedItems = state.resources?.length || 0;
				const newSelectionMap: Record<string, boolean> = {};

				state.resources?.forEach((resource) => {
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
				const newItems = [...(state.resources ?? []), ...payload.data];

				state.resources = newItems;
				state.paginationConfig.canQueryMore = payload.canQueryMore;
				state.paginationConfig.offset =
					state.paginationConfig.offset + payload.data.length;
				state.requestIds.getResources = undefined;
				state.isAllSelected = false;
			})
			.addCase(getResources.rejected, (state) => {
				state.isLoading = false;
				state.hasFetchError = true;
			});
	},
});

const resourcesActions = {
	...resources.actions,
	getResources,
};

export { getResources, resourcesActions };
export default resources.reducer as Reducer<ResourcesStateInterface>;
