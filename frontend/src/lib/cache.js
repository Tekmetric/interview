/**
 * Cache utility for Pokemon data
 * Uses localStorage with expiration
 */

import { logger } from './logger';

const CACHE_PREFIX = 'pokedex_cache_';
const CACHE_VERSION = 'v1';
const DEFAULT_TTL = 24 * 60 * 60 * 1000; // 24 hours in milliseconds

/**
 * Get item from cache
 * @param {string} key - Cache key
 * @returns {any|null} Cached data or null if not found/expired
 */
export const getFromCache = (key) => {
  try {
    const cacheKey = `${CACHE_PREFIX}${CACHE_VERSION}_${key}`;
    const cached = localStorage.getItem(cacheKey);

    if (!cached) {
      return null;
    }

    const { data, expiry } = JSON.parse(cached);

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
 * @param {string} key - Cache key
 * @param {any} data - Data to cache
 * @param {number} ttl - Time to live in milliseconds (default: 24 hours)
 */
export const setInCache = (key, data, ttl = DEFAULT_TTL) => {
  try {
    const cacheKey = `${CACHE_PREFIX}${CACHE_VERSION}_${key}`;
    const expiry = Date.now() + ttl;

    const cacheData = {
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
    if (error.name === 'QuotaExceededError') {
      clearExpiredCache();
      // Try again
      try {
        const cacheKey = `${CACHE_PREFIX}${CACHE_VERSION}_${key}`;
        const expiry = Date.now() + ttl;
        const cacheData = { data, expiry, cachedAt: Date.now() };
        localStorage.setItem(cacheKey, JSON.stringify(cacheData));
      } catch (retryError) {
        logger.error('Failed to cache after cleanup:', retryError);
      }
    }
  }
};

/**
 * Remove item from cache
 * @param {string} key - Cache key
 */
export const removeFromCache = (key) => {
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
export const clearExpiredCache = () => {
  try {
    const keys = Object.keys(localStorage);
    const now = Date.now();

    keys.forEach((key) => {
      if (key.startsWith(CACHE_PREFIX)) {
        try {
          const cached = localStorage.getItem(key);
          if (cached) {
            const { expiry } = JSON.parse(cached);
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
export const clearAllCache = () => {
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
 * @returns {object} Cache stats
 */
export const getCacheStats = () => {
  try {
    const keys = Object.keys(localStorage);
    const cacheKeys = keys.filter(key => key.startsWith(CACHE_PREFIX));
    const now = Date.now();

    let totalSize = 0;
    let validEntries = 0;
    let expiredEntries = 0;

    cacheKeys.forEach((key) => {
      try {
        const cached = localStorage.getItem(key);
        if (cached) {
          totalSize += cached.length;
          const { expiry } = JSON.parse(cached);
          if (now > expiry) {
            expiredEntries++;
          } else {
            validEntries++;
          }
        }
      } catch (error) {
        // Invalid entry
      }
    });

    return {
      totalEntries: cacheKeys.length,
      validEntries,
      expiredEntries,
      totalSizeBytes: totalSize,
      totalSizeKB: (totalSize / 1024).toFixed(2),
    };
  } catch (error) {
    logger.error('Error getting cache stats:', error);
    return null;
  }
};
