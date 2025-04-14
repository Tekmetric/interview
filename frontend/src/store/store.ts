import { configureStore } from '@reduxjs/toolkit';
import { TypedUseSelectorHook, useDispatch, useSelector } from 'react-redux';
import vehicleReducer from '../features/vehicles/reducer';
import applicationReducer from '../features/application/reducer';

const store = configureStore({
  reducer: {
    application: applicationReducer,
    vehicles: vehicleReducer,
  },
});

export type RootState = ReturnType<typeof store.getState>; // Infers the entire state shape
export type AppDispatch = typeof store.dispatch; // Infers the dispatch type

export default store;

// Custom hooks to use with type-safety
export const useAppDispatch = () => useDispatch<AppDispatch>();
export const useAppSelector: TypedUseSelectorHook<RootState> = useSelector;
