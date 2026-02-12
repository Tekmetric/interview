// Re-export cache utilities for external use
export { clearAllCache, clearExpiredCache, getCacheStats } from './cache';

// The data fetching logic has been moved to Redux (pokemonSlice.ts)
// This file now only re-exports cache utilities for backward compatibility
