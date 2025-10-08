/* eslint-disable no-param-reassign */
import { createSlice, type PayloadAction } from '@reduxjs/toolkit';
import { memoize } from 'proxy-memoize';

export type MainPageSlice = {
  activeSortingColumnId: string | undefined;
  sortDirection: string | undefined;
};

const initialState: MainPageSlice = {
  activeSortingColumnId: undefined,
  sortDirection: undefined,
};

export const mainPageSlice = createSlice({
  name: 'main-page',
  initialState,
  reducers: (create) => ({
    setActiveSorting: create.reducer((state, action: PayloadAction<{ columnId: string, sortDirection: string }>) => {
      state.activeSortingColumnId = action.payload.columnId;
      state.sortDirection = action.payload.sortDirection;
    }),
  }),
  selectors: {
    selectActiveSorting: memoize((mainPage) => ({ columnId: mainPage.activeSortingColumnId, sortDirection: mainPage.sortDirection })),
  },
});

export const {
  setActiveSorting,
} = mainPageSlice.actions;

export const { selectActiveSorting } = mainPageSlice.selectors;
