import { configureStore } from '@reduxjs/toolkit';

import loggerMiddleware from './middlewares/logger';
import combinedReducers, { type RootState } from './slices/combinedReducers';

const serializableCheck = {
	serializableCheck: {
		ignoredActions: [],
		ignoredPaths: [],
	},
};

function createStore() {
	return configureStore({
		reducer: combinedReducers,
		middleware: (getDefaultMiddleware) =>
			process.env.NODE_ENV === 'development'
				? getDefaultMiddleware(serializableCheck).concat(loggerMiddleware)
				: getDefaultMiddleware(serializableCheck),
	});
}

export default createStore;
export { RootState };
