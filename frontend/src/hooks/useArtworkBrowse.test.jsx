// @vitest-environment jsdom
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { renderHook, act, cleanup } from '@testing-library/react';
import { MemoryRouter, useLocation, useNavigate } from 'react-router-dom';
import { SEARCH_DEBOUNCE_MS } from '../lib/constants';

// The data hooks hit the network; stub them so this test isolates the URL logic.
vi.mock('./useDepartments', () => ({ useDepartments: () => [] }));
vi.mock('./useArtworkSearch', () => ({
  useArtworkSearch: () => ({ status: 'idle', ids: [], total: 0, error: null, retry: () => {} }),
}));
vi.mock('./usePagedArtworks', () => ({
  usePagedArtworks: () => ({
    items: [],
    status: 'idle',
    failedCount: 0,
    hasMore: false,
    loadMore: () => {},
  }),
}));

import { useArtworkBrowse } from './useArtworkBrowse';

const wrapper = ({ children }) => (
  <MemoryRouter initialEntries={['/']}>{children}</MemoryRouter>
);
const useHarness = () => ({
  browse: useArtworkBrowse(),
  location: useLocation(),
  navigate: useNavigate(),
});

describe('useArtworkBrowse URL sync', () => {
  beforeEach(() => vi.useFakeTimers());
  afterEach(() => {
    vi.useRealTimers();
    cleanup();
  });

  it('commits a typed query to the URL once the debounce settles', () => {
    const { result } = renderHook(useHarness, { wrapper });
    expect(result.current.location.search).toBe('');

    act(() => result.current.browse.setQuery('monet'));
    expect(result.current.location.search).toBe('');

    act(() => vi.advanceTimersByTime(SEARCH_DEBOUNCE_MS));
    expect(result.current.location.search).toBe('?q=monet');
  });

  it('submitQuery commits immediately, without waiting for the debounce', () => {
    const { result } = renderHook(useHarness, { wrapper });
    act(() => result.current.browse.submitQuery('rembrandt'));
    expect(result.current.location.search).toBe('?q=rembrandt');
    expect(result.current.browse.query).toBe('rembrandt');
  });

  it('does not commit queries below the minimum length', () => {
    const { result } = renderHook(useHarness, { wrapper });
    act(() => result.current.browse.setQuery('a'));
    act(() => vi.advanceTimersByTime(SEARCH_DEBOUNCE_MS));
    expect(result.current.location.search).toBe('');
  });

  it('setDepartmentId writes the dept param', () => {
    const { result } = renderHook(useHarness, { wrapper });
    act(() => result.current.browse.setDepartmentId('11'));
    expect(result.current.location.search).toBe('?dept=11');
  });

  it('clears the field when navigation drops the query (e.g. the Search nav link)', () => {
    const { result } = renderHook(useHarness, { wrapper });

    act(() => result.current.browse.submitQuery('monet'));
    expect(result.current.browse.query).toBe('monet');

    // A nav link back to the empty state pushes "/" with no query.
    act(() => result.current.navigate('/'));
    expect(result.current.location.search).toBe('');
    expect(result.current.browse.query).toBe('');
  });

  it('Back restores the previous query into the field without re-committing it', () => {
    const { result } = renderHook(useHarness, { wrapper });

    act(() => result.current.browse.submitQuery('monet'));
    act(() => result.current.browse.submitQuery('rembrandt'));
    expect(result.current.location.search).toBe('?q=rembrandt');

    act(() => result.current.navigate(-1));
    // The field follows the URL back to the earlier search...
    expect(result.current.location.search).toBe('?q=monet');
    expect(result.current.browse.query).toBe('monet');

    // ...and letting the debounce elapse must not push the stale value forward
    // again (the loop the URL-as-source-of-truth design has to avoid).
    act(() => vi.advanceTimersByTime(SEARCH_DEBOUNCE_MS));
    expect(result.current.location.search).toBe('?q=monet');
  });
});
