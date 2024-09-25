import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { Sighting } from "../types/Sighting";

export const DEFAULT_ITEMS_PER_PAGE = 10;

export interface SightingState {
  sightings: Sighting[],
  currentPage: number;
  itemsPerPage: number;
  totalPages: number;
};

const initialState: SightingState = {
  sightings: [],
  currentPage: 0,
  itemsPerPage: DEFAULT_ITEMS_PER_PAGE,
  totalPages: 0,
};

export const sightingSlice = createSlice({
  name: 'sighting',
  initialState,
  reducers: {
    setSightings: (state, action: PayloadAction<Sighting[]>) => {
      state.sightings = action.payload;
    },
    setTotalPages: (state, action: PayloadAction<number>) => {
      state.totalPages = action.payload;
    },
    previousPage: (state) => {
      state.currentPage = Math.max(0, state.currentPage - 1);
    },
    nextPage: (state) => {
      state.currentPage = Math.min(state.totalPages -1, state.currentPage + 1);
    }
  }
})

export const { setSightings, setTotalPages, nextPage, previousPage } = sightingSlice.actions;

export default sightingSlice.reducer
