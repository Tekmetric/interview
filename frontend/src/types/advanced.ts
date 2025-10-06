/**
 * Advanced TypeScript Patterns
 *
 * This file demonstrates the following TypeScript patterns:
 * - Discriminated Unions for type-safe state management
 * - Branded Types for enhanced type safety
 * - Utility Types for flexible type composition
 */

/**
 * Branded Types
 *
 * Creates nominal types to prevent accidentally mixing similar primitive types
 * Example: Prevents using a PokemonId where a UserId is expected
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
  if (id < 1 || id > 1302) {
    throw new Error(`Invalid Pokemon ID: ${id}. Must be between 1 and 1302`);
  }
  return id as PokemonId;
};

/**
 * Extracts the raw number from a PokemonId
 */
export const unwrapPokemonId = (id: PokemonId): number => id as number;

/**
 * Search Query branded type
 */
export type SearchQuery = Brand<string, 'SearchQuery'>;

export const createSearchQuery = (query: string): SearchQuery => {
  return query.trim().toLowerCase() as SearchQuery;
};

/**
 * Discriminated Unions for API States
 *
 * Type-safe state representation that eliminates impossible states
 * Ensures you can't have loading=false and data=null simultaneously
 */

export type LoadingState<T, E = string> =
  | { status: 'idle' }
  | { status: 'loading' }
  | { status: 'success'; data: T }
  | { status: 'error'; error: E };

/**
 * Type guards for discriminated unions
 */
export const isIdle = <T, E>(state: LoadingState<T, E>): state is { status: 'idle' } =>
  state.status === 'idle';

export const isLoading = <T, E>(state: LoadingState<T, E>): state is { status: 'loading' } =>
  state.status === 'loading';

export const isSuccess = <T, E>(
  state: LoadingState<T, E>
): state is { status: 'success'; data: T } => state.status === 'success';

export const isError = <T, E>(
  state: LoadingState<T, E>
): state is { status: 'error'; error: E } => state.status === 'error';

/**
 * Helper to get data safely from LoadingState
 */
export const getLoadingStateData = <T, E>(
  state: LoadingState<T, E>
): T | null => {
  return isSuccess(state) ? state.data : null;
};

/**
 * Helper to get error safely from LoadingState
 */
export const getLoadingStateError = <T, E>(
  state: LoadingState<T, E>
): E | null => {
  return isError(state) ? state.error : null;
};

/**
 * Discriminated Union for Theme Mode
 */
export type ThemeMode =
  | { type: 'light' }
  | { type: 'dark' }
  | { type: 'system'; systemPreference: 'light' | 'dark' };

/**
 * Discriminated Union for Search Filter
 */
export type SearchFilter =
  | { type: 'none' }
  | { type: 'name'; query: string }
  | { type: 'id'; id: PokemonId }
  | { type: 'type'; pokemonType: string };

/**
 * Result Type (similar to Rust's Result<T, E>)
 *
 * Forces explicit error handling
 */
export type Result<T, E = Error> =
  | { ok: true; value: T }
  | { ok: false; error: E };

/**
 * Creates a successful Result
 */
export const Ok = <T>(value: T): Result<T, never> => ({
  ok: true,
  value,
});

/**
 * Creates a failed Result
 */
export const Err = <E>(error: E): Result<never, E> => ({
  ok: false,
  error,
});

/**
 * Type guard for successful Result
 */
export const isOk = <T, E>(result: Result<T, E>): result is { ok: true; value: T } =>
  result.ok === true;

/**
 * Type guard for failed Result
 */
export const isErr = <T, E>(result: Result<T, E>): result is { ok: false; error: E } =>
  result.ok === false;

/**
 * Maps a Result's value if ok, otherwise returns the error
 */
export const mapResult = <T, U, E>(
  result: Result<T, E>,
  fn: (value: T) => U
): Result<U, E> => {
  if (isOk(result)) {
    return Ok(fn(result.value));
  }
  return result as Result<U, E>;
};

/**
 * Non-Empty Array Type
 *
 * Guarantees at least one element exists
 */
export type NonEmptyArray<T> = [T, ...T[]];

/**
 * Type guard for NonEmptyArray
 */
export const isNonEmpty = <T>(arr: T[]): arr is NonEmptyArray<T> =>
  arr.length > 0;

/**
 * Readonly Deep
 *
 * Makes all properties and nested properties readonly
 */
export type DeepReadonly<T> = {
  readonly [P in keyof T]: T[P] extends object
    ? DeepReadonly<T[P]>
    : T[P];
};

/**
 * Exact Type
 *
 * Prevents excess properties
 */
export type Exact<T, Shape> = T extends Shape
  ? Exclude<keyof T, keyof Shape> extends never
    ? T
    : never
  : never;

/**
 * Example usage of advanced patterns:
 *
 * ```typescript
 * // Branded types prevent mixing IDs
 * const pokemonId = createPokemonId(25); // PokemonId
 * const userId = 25; // number
 * // pokemonId and userId are not interchangeable
 *
 * // Discriminated unions eliminate impossible states
 * const state: LoadingState<Pokemon[], string> = { status: 'loading' };
 * if (isSuccess(state)) {
 *   console.log(state.data); // TypeScript knows data exists
 * }
 *
 * // Result type forces error handling
 * const result = fetchPokemon(25);
 * if (isOk(result)) {
 *   console.log(result.value.name);
 * } else {
 *   console.error(result.error);
 * }
 * ```
 */
