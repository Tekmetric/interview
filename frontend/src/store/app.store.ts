import { configureStore } from "@reduxjs/toolkit";
import sessionReducer from './session.store';
import sightingsReducer from './sightings.store';

export const store = configureStore({
  reducer: {
    session: sessionReducer,
    sightings: sightingsReducer,
  }
});

export type AppStore = typeof store;
export type AppDispatch = typeof store.dispatch;
export type RootState = ReturnType<typeof store.getState>;
