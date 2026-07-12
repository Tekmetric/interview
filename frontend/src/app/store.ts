import { combineReducers, configureStore, createListenerMiddleware } from '@reduxjs/toolkit';

import { rickAndMortyApi } from '../api/rickAndMortyApi';
import {
  favoritesSlice,
  favoriteToggled,
  FAVORITES_STORAGE_KEY,
} from '../features/favorites/favoritesSlice';
import { localeChanged, localeSlice, LOCALE_STORAGE_KEY } from '../features/locale/localeSlice';
import { themeSlice, themeToggled, THEME_STORAGE_KEY } from '../features/theme/themeSlice';
import { writeStorage } from '../utils/storage';

const rootReducer = combineReducers({
  [rickAndMortyApi.reducerPath]: rickAndMortyApi.reducer,
  [themeSlice.reducerPath]: themeSlice.reducer,
  [localeSlice.reducerPath]: localeSlice.reducer,
  [favoritesSlice.reducerPath]: favoritesSlice.reducer,
});

export type RootState = ReturnType<typeof rootReducer>;
export type AppStore = ReturnType<typeof makeStore>;
export type AppDispatch = AppStore['dispatch'];

// Factory instead of a singleton so every test gets an isolated store
// (with the persistence listeners included, exactly like production).
export function makeStore(preloadedState?: Partial<RootState>) {
  const listenerMiddleware = createListenerMiddleware();
  const startAppListening = listenerMiddleware.startListening.withTypes<RootState, AppDispatch>();

  // Persist UI preferences as they change. A listener keeps the side effect
  // out of the reducers (which must stay pure) and out of the components
  // (which shouldn't know about persistence).
  startAppListening({
    actionCreator: themeToggled,
    effect: (_action, api) => writeStorage(THEME_STORAGE_KEY, api.getState().theme.mode),
  });
  startAppListening({
    actionCreator: localeChanged,
    effect: (action) => writeStorage(LOCALE_STORAGE_KEY, action.payload),
  });
  startAppListening({
    actionCreator: favoriteToggled,
    effect: (_action, api) =>
      writeStorage(FAVORITES_STORAGE_KEY, JSON.stringify(api.getState().favorites)),
  });

  return configureStore({
    reducer: rootReducer,
    preloadedState,
    // The api middleware handles cache lifetimes, polling and invalidation.
    middleware: (getDefaultMiddleware) =>
      getDefaultMiddleware()
        .prepend(listenerMiddleware.middleware)
        .concat(rickAndMortyApi.middleware),
  });
}

export const store = makeStore();
