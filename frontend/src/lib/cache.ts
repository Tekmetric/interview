/**
 * Cache utility for Pokemon data
 * Uses localStorage with expiration
 */

import { logger } from './logger';

const CACHE_PREFIX = 'pokedex_cache_';
const CACHE_VERSION = 'v1';

// Time constants
const HOURS_IN_DAY = 24;
const MINUTES_IN_HOUR = 60;
const SECONDS_IN_MINUTE = 60;
const MS_IN_SECOND = 1000;
const DEFAULT_TTL = HOURS_IN_DAY * MINUTES_IN_HOUR * SECONDS_IN_MINUTE * MS_IN_SECOND; // 24 hours in milliseconds

interface CacheEntry<T> {
  data: T;
  expiry: number;
  cachedAt: number;
}

export interface CacheStats {
  totalEntries: number;
  validEntries: number;
  expiredEntries: number;
  invalidEntries: number;
  totalSizeBytes: number;
  totalSizeKB: string;
}

/**
 * Get item from cache
 */
export const getFromCache = <T = any>(key: string): T | null => {
  try {
    const cacheKey = `${CACHE_PREFIX}${CACHE_VERSION}_${key}`;
    const cached = localStorage.getItem(cacheKey);

    if (!cached) {
      return null;
    }

    const { data, expiry } = JSON.parse(cached) as CacheEntry<T>;

    // Check if expired
    if (Date.now() > expiry) {
      localStorage.removeItem(cacheKey);
      return null;
    }

    return data;
  } catch (error) {
    logger.error('Error reading from cache:', error);
    return null;
  }
};

/**
 * Set item in cache with expiration
 */
export const setInCache = <T = any>(key: string, data: T, ttl: number = DEFAULT_TTL): void => {
  try {
    const cacheKey = `${CACHE_PREFIX}${CACHE_VERSION}_${key}`;
    const expiry = Date.now() + ttl;

    const cacheData: CacheEntry<T> = {
      data,
      expiry,
      cachedAt: Date.now(),
    };

    const serialized = JSON.stringify(cacheData);
    logger.debug(`Attempting to cache ${key}: ${(serialized.length / 1024 / 1024).toFixed(2)} MB`);

    localStorage.setItem(cacheKey, serialized);
    logger.debug(`Successfully cached ${key}`);
  } catch (error) {
    logger.error('Error writing to cache:', error);
    // If quota exceeded, clear old cache entries
    if (error instanceof Error && error.name === 'QuotaExceededError') {
      clearExpiredCache();
      // Try again
      try {
        const cacheKey = `${CACHE_PREFIX}${CACHE_VERSION}_${key}`;
        const expiry = Date.now() + ttl;
        const cacheData: CacheEntry<T> = { data, expiry, cachedAt: Date.now() };
        localStorage.setItem(cacheKey, JSON.stringify(cacheData));
      } catch (retryError) {
        logger.error('Failed to cache after cleanup:', retryError);
      }
    }
  }
};

/**
 * Remove item from cache
 */
export const removeFromCache = (key: string): void => {
  try {
    const cacheKey = `${CACHE_PREFIX}${CACHE_VERSION}_${key}`;
    localStorage.removeItem(cacheKey);
  } catch (error) {
    logger.error('Error removing from cache:', error);
  }
};

/**
 * Clear all expired cache entries
 */
export const clearExpiredCache = (): void => {
  try {
    const keys = Object.keys(localStorage);
    const now = Date.now();

    keys.forEach((key) => {
      if (key.startsWith(CACHE_PREFIX)) {
        try {
          const cached = localStorage.getItem(key);
          if (cached) {
            const { expiry } = JSON.parse(cached) as CacheEntry<any>;
            if (now > expiry) {
              localStorage.removeItem(key);
            }
          }
        } catch (error) {
          // Invalid cache entry, remove it
          localStorage.removeItem(key);
        }
      }
    });
  } catch (error) {
    logger.error('Error clearing expired cache:', error);
  }
};

/**
 * Clear all cache entries (including non-expired)
 */
export const clearAllCache = (): void => {
  try {
    const keys = Object.keys(localStorage);
    keys.forEach((key) => {
      if (key.startsWith(CACHE_PREFIX)) {
        localStorage.removeItem(key);
      }
    });
  } catch (error) {
    logger.error('Error clearing all cache:', error);
  }
};

/**
 * Get cache statistics
 */
export const getCacheStats = (): CacheStats | null => {
  try {
    const keys = Object.keys(localStorage);
    const cacheKeys = keys.filter(key => key.startsWith(CACHE_PREFIX));
    const now = Date.now();

    let totalSize = 0;
    let validEntries = 0;
    let expiredEntries = 0;
    let invalidEntries = 0;

    cacheKeys.forEach((key) => {
      try {
        const cached = localStorage.getItem(key);
        if (cached) {
          totalSize += cached.length;
          const { expiry } = JSON.parse(cached) as CacheEntry<any>;
          if (now > expiry) {
            expiredEntries++;
          } else {
            validEntries++;
          }
        }
      } catch (error) {
        // Invalid entry - log and remove it
        logger.warn('Invalid cache entry found, removing:', { key, error });
        localStorage.removeItem(key);
        invalidEntries++;
      }
    });

    return {
      totalEntries: cacheKeys.length,
      validEntries,
      expiredEntries,
      invalidEntries,
      totalSizeBytes: totalSize,
      totalSizeKB: (totalSize / 1024).toFixed(2),
    };
  } catch (error) {
    logger.error('Error getting cache stats:', error);
    return null;
  }
};
