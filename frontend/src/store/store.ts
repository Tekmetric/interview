import { configureStore } from '@reduxjs/toolkit';
import pokemonReducer from './pokemonSlice';
import themeReducer from './themeSlice';

export const store = configureStore({
  reducer: {
    pokemon: pokemonReducer,
    theme: themeReducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: {
        // Ignore these action types
        ignoredActions: ['pokemon/fetchData/pending', 'pokemon/fetchData/fulfilled'],
      },
    }),
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
