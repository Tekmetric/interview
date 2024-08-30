import { configureStore, Store } from '@reduxjs/toolkit';
import rootReducer from './rootReducer';

const store: Store = configureStore({
  reducer: rootReducer,
  devTools: process.env.NODE_ENV === 'development',
});

export default store;
