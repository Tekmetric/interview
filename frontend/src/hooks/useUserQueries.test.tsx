import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { renderHook } from '@testing-library/react';
import { beforeEach, describe, expect, it, vi } from 'vitest';

import { userKeys, useUsers } from './useUserQueries';

// Mock the ApiService
vi.mock('../services/ApiService', () => ({
  ApiService: {
    fetchUsers: vi.fn(),
  },
}));

// Test wrapper for React Query
const createWrapper = () => {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: {
        retry: false,
        staleTime: Infinity,
      },
    },
  });

  return ({ children }: { children: React.ReactNode }) => (
    <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
  );
};

describe('useUserQueries', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('userKeys', () => {
    it('generates correct query keys', () => {
      expect(userKeys.lists()).toEqual(['users', 'list']);
      expect(userKeys.detail('123')).toEqual(['users', 'detail', '123']);
    });
  });

  describe('useUsers hook', () => {
    it('initializes with default state', () => {
      const wrapper = createWrapper();
      const { result } = renderHook(() => useUsers(), { wrapper });

      // Check initial filter state
      expect(result.current.filterConfig).toEqual({
        searchTerm: '',
        statusFilter: '',
      });

      // Check initial sort state
      expect(result.current.sortConfig).toEqual({
        key: 'name',
        direction: 'asc',
      });

      // Check that pagination exists and has expected structure
      expect(result.current.pagination).toMatchObject({
        currentPage: expect.any(Number),
        totalPages: expect.any(Number),
        totalRecords: expect.any(Number),
        pageSize: expect.any(Number),
      });
    });

    it('provides expected hook interface', () => {
      const wrapper = createWrapper();
      const { result } = renderHook(() => useUsers(), { wrapper });

      // Check that all expected functions exist
      expect(typeof result.current.updateFilters).toBe('function');
      expect(typeof result.current.handleSort).toBe('function');
      expect(typeof result.current.goToPage).toBe('function');
      expect(typeof result.current.changePageSize).toBe('function');
      expect(typeof result.current.refetch).toBe('function');

      // Check that data properties exist
      expect(Array.isArray(result.current.data)).toBe(true);
      expect(Array.isArray(result.current.allData)).toBe(true);
      expect(typeof result.current.isLoading).toBe('boolean');
    });

    it('returns empty data arrays initially', () => {
      const wrapper = createWrapper();
      const { result } = renderHook(() => useUsers(), { wrapper });

      expect(result.current.data).toEqual([]);
      expect(result.current.allData).toEqual([]);
    });
  });
});
