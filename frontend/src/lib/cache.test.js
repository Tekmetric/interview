import {
  getFromCache,
  setInCache,
  removeFromCache,
  clearExpiredCache,
  clearAllCache,
  getCacheStats,
} from './cache';

describe('Cache utility', () => {
  beforeEach(() => {
    localStorage.clear();
    jest.clearAllMocks();
  });

  describe('getFromCache', () => {
    test('returns null when cache key does not exist', () => {
      const result = getFromCache('nonexistent');
      expect(result).toBeNull();
    });

    test('returns cached data when valid', () => {
      const testData = { id: 1, name: 'test' };
      setInCache('test-key', testData);

      const result = getFromCache('test-key');
      expect(result).toEqual(testData);
    });

    test('returns null and removes expired cache', () => {
      const testData = { id: 1, name: 'test' };
      setInCache('test-key', testData, 1); // 1ms TTL

      return new Promise((resolve) => {
        setTimeout(() => {
          const result = getFromCache('test-key');
          expect(result).toBeNull();
          resolve();
        }, 10);
      });
    });

    test('returns null on JSON parse error', () => {
      const consoleErrorSpy = jest.spyOn(console, 'error').mockImplementation(() => {});

      localStorage.setItem('pokedex_cache_v1_corrupt', 'invalid json');
      const result = getFromCache('corrupt');
      expect(result).toBeNull();

      consoleErrorSpy.mockRestore();
    });

    test('handles localStorage.getItem error gracefully', () => {
      const consoleErrorSpy = jest.spyOn(console, 'error').mockImplementation(() => {});

      jest.spyOn(Storage.prototype, 'getItem').mockImplementation(() => {
        throw new Error('Storage error');
      });

      const result = getFromCache('test-key');
      expect(result).toBeNull();

      consoleErrorSpy.mockRestore();
      jest.restoreAllMocks();
    });
  });

  describe('setInCache', () => {
    test('stores data with expiry', () => {
      const testData = { id: 1, name: 'test' };
      setInCache('test-key', testData);

      const result = getFromCache('test-key');
      expect(result).toEqual(testData);
    });

    test('stores data with custom TTL', () => {
      const testData = { id: 1, name: 'test' };
      setInCache('test-key', testData, 5000);

      const result = getFromCache('test-key');
      expect(result).toEqual(testData);
    });

    test('handles QuotaExceededError by clearing cache and retrying', () => {
      const testData = { id: 1, name: 'test' };
      let callCount = 0;

      jest.spyOn(Storage.prototype, 'setItem').mockImplementation((key, value) => {
        callCount++;
        if (callCount === 1) {
          const error = new Error('QuotaExceededError');
          error.name = 'QuotaExceededError';
          throw error;
        }
        // Second call succeeds (after clearExpiredCache)
        localStorage.__proto__.setItem.call(localStorage, key, value);
      });

      setInCache('test-key', testData);
      expect(callCount).toBe(2);

      jest.restoreAllMocks();
    });

    test('handles QuotaExceededError retry failure', () => {
      const testData = { id: 1, name: 'test' };
      const consoleErrorSpy = jest.spyOn(console, 'error').mockImplementation(() => {});

      jest.spyOn(Storage.prototype, 'setItem').mockImplementation(() => {
        const error = new Error('QuotaExceededError');
        error.name = 'QuotaExceededError';
        throw error;
      });

      setInCache('test-key', testData);

      expect(consoleErrorSpy).toHaveBeenCalled();

      jest.restoreAllMocks();
    });

    test('handles other storage errors gracefully', () => {
      const testData = { id: 1, name: 'test' };
      const consoleErrorSpy = jest.spyOn(console, 'error').mockImplementation(() => {});

      jest.spyOn(Storage.prototype, 'setItem').mockImplementation(() => {
        throw new Error('Generic storage error');
      });

      setInCache('test-key', testData);

      expect(consoleErrorSpy).toHaveBeenCalled();

      jest.restoreAllMocks();
    });
  });

  describe('removeFromCache', () => {
    test('removes item from cache', () => {
      const testData = { id: 1, name: 'test' };
      setInCache('test-key', testData);

      expect(getFromCache('test-key')).toEqual(testData);

      removeFromCache('test-key');
      expect(getFromCache('test-key')).toBeNull();
    });

    test('handles removeItem error gracefully', () => {
      const consoleErrorSpy = jest.spyOn(console, 'error').mockImplementation(() => {});

      jest.spyOn(Storage.prototype, 'removeItem').mockImplementation(() => {
        throw new Error('Remove error');
      });

      removeFromCache('test-key');

      expect(consoleErrorSpy).toHaveBeenCalled();

      jest.restoreAllMocks();
    });
  });

  describe('clearExpiredCache', () => {
    test('removes only expired entries', () => {
      const validData = { id: 1, name: 'valid' };
      const expiredData = { id: 2, name: 'expired' };

      setInCache('valid-key', validData, 10000); // 10 seconds
      setInCache('expired-key', expiredData, 1); // 1ms

      return new Promise((resolve) => {
        setTimeout(() => {
          clearExpiredCache();

          expect(getFromCache('valid-key')).toEqual(validData);
          expect(getFromCache('expired-key')).toBeNull();
          resolve();
        }, 10);
      });
    });

    test('removes invalid cache entries', () => {
      localStorage.setItem('pokedex_cache_v1_invalid', 'not valid json');

      clearExpiredCache();

      expect(localStorage.getItem('pokedex_cache_v1_invalid')).toBeNull();
    });

    test('does not remove non-cache entries', () => {
      localStorage.setItem('other-key', 'other-value');
      setInCache('cache-key', { data: 'test' }, 1);

      return new Promise((resolve) => {
        setTimeout(() => {
          clearExpiredCache();

          expect(localStorage.getItem('other-key')).toBe('other-value');
          resolve();
        }, 10);
      });
    });

    test('handles errors gracefully', () => {
      const consoleErrorSpy = jest.spyOn(console, 'error').mockImplementation(() => {});

      jest.spyOn(Storage.prototype, 'getItem').mockImplementation(() => {
        throw new Error('Storage error');
      });

      clearExpiredCache();

      expect(consoleErrorSpy).toHaveBeenCalled();

      jest.restoreAllMocks();
    });
  });

  describe('clearAllCache', () => {
    test('removes all cache entries', () => {
      setInCache('key1', { data: 'test1' });
      setInCache('key2', { data: 'test2' });
      setInCache('key3', { data: 'test3' });

      expect(getFromCache('key1')).not.toBeNull();
      expect(getFromCache('key2')).not.toBeNull();
      expect(getFromCache('key3')).not.toBeNull();

      clearAllCache();

      expect(getFromCache('key1')).toBeNull();
      expect(getFromCache('key2')).toBeNull();
      expect(getFromCache('key3')).toBeNull();
    });

    test('does not remove non-cache entries', () => {
      localStorage.setItem('other-key', 'other-value');
      setInCache('cache-key', { data: 'test' });

      clearAllCache();

      expect(localStorage.getItem('other-key')).toBe('other-value');
      expect(getFromCache('cache-key')).toBeNull();
    });

    test('handles errors gracefully', () => {
      const consoleErrorSpy = jest.spyOn(console, 'error').mockImplementation(() => {});

      jest.spyOn(Storage.prototype, 'removeItem').mockImplementation(() => {
        throw new Error('Remove error');
      });

      setInCache('test-key', { data: 'test' });
      clearAllCache();

      expect(consoleErrorSpy).toHaveBeenCalled();

      jest.restoreAllMocks();
    });
  });

  describe('getCacheStats', () => {
    test('returns correct stats for valid cache', () => {
      setInCache('key1', { data: 'test1' });
      setInCache('key2', { data: 'test2' });

      const stats = getCacheStats();

      expect(stats.totalEntries).toBe(2);
      expect(stats.validEntries).toBe(2);
      expect(stats.expiredEntries).toBe(0);
      expect(stats.totalSizeBytes).toBeGreaterThan(0);
      expect(stats.totalSizeKB).toBeDefined();
    });

    test('returns correct stats with expired entries', () => {
      setInCache('valid', { data: 'test1' }, 10000);
      setInCache('expired', { data: 'test2' }, 1);

      return new Promise((resolve) => {
        setTimeout(() => {
          const stats = getCacheStats();

          expect(stats.totalEntries).toBe(2);
          expect(stats.validEntries).toBe(1);
          expect(stats.expiredEntries).toBe(1);
          resolve();
        }, 10);
      });
    });

    test('ignores invalid cache entries in stats', () => {
      localStorage.setItem('pokedex_cache_v1_invalid', 'not valid json');
      setInCache('valid', { data: 'test' });

      const stats = getCacheStats();

      // Should only count valid entry
      expect(stats.validEntries).toBe(1);
    });

    test('returns null on error', () => {
      const consoleErrorSpy = jest.spyOn(console, 'error').mockImplementation(() => {});

      jest.spyOn(Storage.prototype, 'getItem').mockImplementation(() => {
        throw new Error('Storage error');
      });

      const stats = getCacheStats();

      expect(stats).toBeNull();
      expect(consoleErrorSpy).toHaveBeenCalled();

      jest.restoreAllMocks();
    });

    test('does not count non-cache keys', () => {
      localStorage.setItem('other-key', 'other-value');
      setInCache('cache-key', { data: 'test' });

      const stats = getCacheStats();

      expect(stats.totalEntries).toBe(1);
    });

    test('calculates size correctly', () => {
      const largeData = { data: 'x'.repeat(1000) };
      setInCache('large', largeData);

      const stats = getCacheStats();

      expect(stats.totalSizeBytes).toBeGreaterThan(1000);
      expect(parseFloat(stats.totalSizeKB)).toBeGreaterThan(1);
    });
  });
});
