import {
  selectPokemonData,
  selectPokemonLoading,
  selectPokemonError,
  selectSearchTerm,
  selectIsMetric,
  selectIsDarkMode,
  selectFilteredPokemon,
  selectPokemonCount,
  selectFilteredPokemonCount,
  selectIsSearching,
  selectPokemonById,
  selectAllPokemonTypes,
} from './selectors';
import type { RootState } from './store';
import type { Pokemon } from '../types/pokemon';

describe('selectors', () => {
  const mockPokemon: Pokemon[] = [
    {
      id: 1,
      name: 'bulbasaur',
      height: 7,
      weight: 69,
      sprites: { front_default: 'bulbasaur.png' },
      types: [{ type: { name: 'grass' } }, { type: { name: 'poison' } }],
      stats: [{ stat: { name: 'hp' }, base_stat: 45 }],
    },
    {
      id: 25,
      name: 'pikachu',
      height: 4,
      weight: 60,
      sprites: { front_default: 'pikachu.png' },
      types: [{ type: { name: 'electric' } }],
      stats: [{ stat: { name: 'hp' }, base_stat: 35 }],
    },
    {
      id: 4,
      name: 'charmander',
      height: 6,
      weight: 85,
      sprites: { front_default: 'charmander.png' },
      types: [{ type: { name: 'fire' } }],
      stats: [{ stat: { name: 'hp' }, base_stat: 39 }],
    },
  ];

  const mockState: RootState = {
    pokemon: {
      data: mockPokemon,
      loading: false,
      error: null,
      searchTerm: '',
      isMetric: true,
    },
    theme: {
      isDarkMode: false,
    },
  };

  describe('base selectors', () => {
    it('should select pokemon data', () => {
      expect(selectPokemonData(mockState)).toEqual(mockPokemon);
    });

    it('should select loading state', () => {
      expect(selectPokemonLoading(mockState)).toBe(false);
    });

    it('should select error', () => {
      expect(selectPokemonError(mockState)).toBe(null);
    });

    it('should select search term', () => {
      expect(selectSearchTerm(mockState)).toBe('');
    });

    it('should select isMetric', () => {
      expect(selectIsMetric(mockState)).toBe(true);
    });

    it('should select isDarkMode', () => {
      expect(selectIsDarkMode(mockState)).toBe(false);
    });
  });

  describe('selectFilteredPokemon', () => {
    it('should return all pokemon when search term is empty', () => {
      const result = selectFilteredPokemon(mockState);
      expect(result).toEqual(mockPokemon);
    });

    it('should filter pokemon by name', () => {
      const stateWithSearch = {
        ...mockState,
        pokemon: { ...mockState.pokemon, searchTerm: 'pika' },
      };
      const result = selectFilteredPokemon(stateWithSearch);
      expect(result).toHaveLength(1);
      expect(result[0].name).toBe('pikachu');
    });

    it('should filter pokemon by ID', () => {
      const stateWithSearch = {
        ...mockState,
        pokemon: { ...mockState.pokemon, searchTerm: '25' },
      };
      const result = selectFilteredPokemon(stateWithSearch);
      expect(result).toHaveLength(1);
      expect(result[0].id).toBe(25);
    });

    it('should filter pokemon by type', () => {
      const stateWithSearch = {
        ...mockState,
        pokemon: { ...mockState.pokemon, searchTerm: 'fire' },
      };
      const result = selectFilteredPokemon(stateWithSearch);
      expect(result).toHaveLength(1);
      expect(result[0].name).toBe('charmander');
    });

    it('should be case insensitive', () => {
      const stateWithSearch = {
        ...mockState,
        pokemon: { ...mockState.pokemon, searchTerm: 'PIKA' },
      };
      const result = selectFilteredPokemon(stateWithSearch);
      expect(result).toHaveLength(1);
      expect(result[0].name).toBe('pikachu');
    });
  });

  describe('selectPokemonCount', () => {
    it('should return total pokemon count', () => {
      expect(selectPokemonCount(mockState)).toBe(3);
    });

    it('should return 0 for empty data', () => {
      const emptyState = {
        ...mockState,
        pokemon: { ...mockState.pokemon, data: [] },
      };
      expect(selectPokemonCount(emptyState)).toBe(0);
    });
  });

  describe('selectFilteredPokemonCount', () => {
    it('should return filtered pokemon count', () => {
      const stateWithSearch = {
        ...mockState,
        pokemon: { ...mockState.pokemon, searchTerm: 'pika' },
      };
      expect(selectFilteredPokemonCount(stateWithSearch)).toBe(1);
    });

    it('should return total count when no search', () => {
      expect(selectFilteredPokemonCount(mockState)).toBe(3);
    });
  });

  describe('selectIsSearching', () => {
    it('should return false when search term is empty', () => {
      expect(selectIsSearching(mockState)).toBe(false);
    });

    it('should return true when search term exists', () => {
      const stateWithSearch = {
        ...mockState,
        pokemon: { ...mockState.pokemon, searchTerm: 'pika' },
      };
      expect(selectIsSearching(stateWithSearch)).toBe(true);
    });
  });

  describe('selectPokemonById', () => {
    it('should return pokemon by id', () => {
      const pokemon = selectPokemonById(25)(mockState);
      expect(pokemon?.name).toBe('pikachu');
    });

    it('should return undefined for non-existent id', () => {
      const pokemon = selectPokemonById(999)(mockState);
      expect(pokemon).toBeUndefined();
    });
  });

  describe('selectAllPokemonTypes', () => {
    it('should return all unique types sorted', () => {
      const types = selectAllPokemonTypes(mockState);
      expect(types).toEqual(['electric', 'fire', 'grass', 'poison']);
    });

    it('should not include duplicates', () => {
      const types = selectAllPokemonTypes(mockState);
      const uniqueTypes = new Set(types);
      expect(types.length).toBe(uniqueTypes.size);
    });
  });

  describe('memoization', () => {
    it('should memoize selectFilteredPokemon', () => {
      const result1 = selectFilteredPokemon(mockState);
      const result2 = selectFilteredPokemon(mockState);
      expect(result1).toBe(result2); // Same reference
    });

    it('should recompute when dependencies change', () => {
      const result1 = selectFilteredPokemon(mockState);
      const stateWithSearch = {
        ...mockState,
        pokemon: { ...mockState.pokemon, searchTerm: 'pika' },
      };
      const result2 = selectFilteredPokemon(stateWithSearch);
      expect(result1).not.toBe(result2); // Different reference
      expect(result1.length).toBe(3);
      expect(result2.length).toBe(1);
    });
  });
});
