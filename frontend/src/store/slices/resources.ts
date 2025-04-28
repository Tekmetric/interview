import { createSlice, type Reducer } from '@reduxjs/toolkit';

interface ResourceInterface {
	id: string;
	title: string;
	type: 'book' | 'video' | 'article' | 'repository' | 'course';
	link: string;
	description?: string;
	tags?: string[];
	createdAt: string;
}

export interface ResourcesStateInterface {
	resources: ResourceInterface[] | undefined;
	paginationConfig: {
		offset: number;
		limit: number;
		canQueryMore: boolean;
	};
	hasFetchError: boolean;
	isLoading: boolean;
	isSelectedMap: Record<string, boolean>;
	numberOfSelectedItems: number;
	isAllSelected: boolean;
}

const initialState: ResourcesStateInterface = {
	resources: undefined,
	paginationConfig: {
		offset: 0,
		limit: 10,
		canQueryMore: true,
	},
	hasFetchError: false,
	isLoading: false,
	isSelectedMap: {},
	numberOfSelectedItems: 0,
	isAllSelected: false,
};

const resources = createSlice({
	name: 'resources',
	initialState,
	reducers: {},
});

const resourcesActions = {
	...resources.actions,
};

export { resourcesActions };
export default resources.reducer as Reducer<ResourcesStateInterface>;
