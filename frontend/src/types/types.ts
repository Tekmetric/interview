/**
 * Advanced TypeScript Patterns
 *
 * Utility types and patterns for enhanced type safety
 * Note: This file is retained for potential future use
 * RTK Query handles most state management, making some patterns unnecessary
 */

// Brand symbol for uniqueness
declare const brand: unique symbol;

type Brand<T, TBrand extends string> = T & { [brand]: TBrand };

/**
 * Pokemon ID branded type - ensures type safety
 */
export type PokemonId = Brand<number, 'PokemonId'>;

/**
 * Creates a branded PokemonId from a number
 */
export const createPokemonId = (id: number): PokemonId => {
  if (id < 1 || id > 1025) {
    throw new Error(`Invalid Pokemon ID: ${id}. Must be between 1 and 1025`);
  }
  return id as PokemonId;
};

/**
 * Extracts the raw number from a PokemonId
 */
export const unwrapPokemonId = (id: PokemonId): number => id as number;
