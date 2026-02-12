import pokemonReducer, {
  setSearchTerm,
  toggleMetric,
  setMetric,
  fetchPokemonData,
  PokemonState
} from './pokemonSlice';
import { Pokemon } from '../types/pokemon';

describe('pokemonSlice', () => {
  const initialState: PokemonState = {
    data: [],
    loading: false,
    error: null,
    searchTerm: '',
    isMetric: true,
  };

  it('should return the initial state', () => {
    expect(pokemonReducer(undefined, { type: 'unknown' })).toEqual(initialState);
  });

  describe('synchronous actions', () => {
    it('should handle setSearchTerm', () => {
      const state = pokemonReducer(initialState, setSearchTerm('pikachu'));
      expect(state.searchTerm).toBe('pikachu');
    });

    it('should handle toggleMetric', () => {
      const state = pokemonReducer(initialState, toggleMetric());
      expect(state.isMetric).toBe(false);

      const toggledState = pokemonReducer(state, toggleMetric());
      expect(toggledState.isMetric).toBe(true);
    });

    it('should handle setMetric with true', () => {
      const state = pokemonReducer(initialState, setMetric(true));
      expect(state.isMetric).toBe(true);
    });

    it('should handle setMetric with false', () => {
      const state = pokemonReducer(initialState, setMetric(false));
      expect(state.isMetric).toBe(false);
    });
  });

  describe('async actions - fetchPokemonData', () => {
    const mockPokemon: Pokemon[] = [
      {
        id: 1,
        name: 'bulbasaur',
        height: 7,
        weight: 69,
        sprites: { front_default: 'https://example.com/bulbasaur.png' },
        types: [{ type: { name: 'grass' } }],
        stats: [
          { stat: { name: 'hp' }, base_stat: 45 },
          { stat: { name: 'attack' }, base_stat: 49 },
        ],
      },
    ];

    it('should handle fetchPokemonData.pending', () => {
      const action = { type: fetchPokemonData.pending.type };
      const state = pokemonReducer(initialState, action);
      expect(state.loading).toBe(true);
      expect(state.error).toBe(null);
    });

    it('should handle fetchPokemonData.fulfilled', () => {
      const action = {
        type: fetchPokemonData.fulfilled.type,
        payload: mockPokemon,
      };
      const state = pokemonReducer(initialState, action);
      expect(state.loading).toBe(false);
      expect(state.data).toEqual(mockPokemon);
      expect(state.error).toBe(null);
    });

    it('should handle fetchPokemonData.rejected', () => {
      const action = {
        type: fetchPokemonData.rejected.type,
        payload: 'Failed to load Pokemon',
      };
      const state = pokemonReducer(initialState, action);
      expect(state.loading).toBe(false);
      expect(state.error).toBe('Failed to load Pokemon');
      expect(state.data).toEqual([]);
    });

    it('should clear error when starting new fetch', () => {
      const errorState: PokemonState = {
        ...initialState,
        error: 'Previous error',
      };
      const action = { type: fetchPokemonData.pending.type };
      const state = pokemonReducer(errorState, action);
      expect(state.error).toBe(null);
      expect(state.loading).toBe(true);
    });

    it('should maintain search term during fetch', () => {
      const searchState: PokemonState = {
        ...initialState,
        searchTerm: 'pikachu',
      };
      const action = { type: fetchPokemonData.pending.type };
      const state = pokemonReducer(searchState, action);
      expect(state.searchTerm).toBe('pikachu');
    });
  });
});
