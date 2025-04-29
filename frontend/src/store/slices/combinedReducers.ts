import { combineReducers } from '@reduxjs/toolkit';

import ResourcesReducer from './resources';

const combinedReducers = combineReducers({
	resources: ResourcesReducer,
});

export type RootState = ReturnType<typeof combinedReducers>;
export default combinedReducers;
