import { configureStore } from '@reduxjs/toolkit';
import { Reducer, UnknownAction } from 'redux';
import { TypedUseSelectorHook, useDispatch, useSelector } from 'react-redux';
import { ApplicationState } from '../features/application/types';
import { VehiclesState } from '../features/vehicles/types';
import vehicleReducer from '../features/vehicles/reducer';
import applicationReducer from '../features/application/reducer';

// Casting reducers to fix RTK type mismatch with classic reducer setup
const store = configureStore({
  reducer: {
    application: applicationReducer as Reducer<ApplicationState, UnknownAction>,
    vehicles: vehicleReducer as Reducer<VehiclesState, UnknownAction>,
  },
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;

export default store;

export const useAppDispatch = () => useDispatch<AppDispatch>();
export const useAppSelector: TypedUseSelectorHook<RootState> = useSelector;
