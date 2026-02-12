import { createSlice, PayloadAction } from '@reduxjs/toolkit';

/**
 * Pokemon Slice
 *
 * Manages Pokemon-related state (search, units, etc.)
 * Note: Pokemon data fetching is handled by RTK Query (src/store/api.ts)
 */

export interface PokemonState {
  searchTerm: string;
  isMetric: boolean;
}

const initialState: PokemonState = {
  searchTerm: '',
  isMetric: true,
};

const pokemonSlice = createSlice({
  name: 'pokemon',
  initialState,
  reducers: {
    setSearchTerm: (state, action: PayloadAction<string>) => {
      state.searchTerm = action.payload;
    },
    toggleMetric: (state) => {
      state.isMetric = !state.isMetric;
    },
    setMetric: (state, action: PayloadAction<boolean>) => {
      state.isMetric = action.payload;
    },
  },
});

export const { setSearchTerm, toggleMetric, setMetric } = pokemonSlice.actions;
export default pokemonSlice.reducer;
