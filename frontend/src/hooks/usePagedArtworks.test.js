// @vitest-environment jsdom
import { describe, it, expect, vi, afterEach } from 'vitest';
import { renderHook, act, waitFor, cleanup } from '@testing-library/react';

vi.mock('../api/metMuseum', () => ({ fetchObject: vi.fn() }));
import { fetchObject } from '../api/metMuseum';
import { usePagedArtworks } from './usePagedArtworks';

const art = (id) => ({ id, title: `Art ${id}` });

afterEach(() => {
  cleanup();
  vi.clearAllMocks();
});

describe('usePagedArtworks', () => {
  it('loads a page and counts failures without dropping the survivors', async () => {
    fetchObject.mockImplementation((id) =>
      id === 2 ? Promise.reject(new Error('404')) : Promise.resolve(art(id))
    );

    // Stable reference: the hook re-fetches whenever the ids array identity
    // changes, so an inline literal here would loop.
    const ids = [1, 2, 3];
    const { result } = renderHook(() => usePagedArtworks(ids, 3));

    await waitFor(() => expect(result.current.status).toBe('success'));
    expect(result.current.items.map((a) => a.id)).toEqual([1, 3]);
    expect(result.current.failedCount).toBe(1);
    expect(result.current.hasMore).toBe(false);
  });

  it('appends the next page on loadMore', async () => {
    fetchObject.mockImplementation((id) => Promise.resolve(art(id)));

    const ids = [1, 2, 3, 4];
    const { result } = renderHook(() => usePagedArtworks(ids, 2));
    await waitFor(() => expect(result.current.items.map((a) => a.id)).toEqual([1, 2]));
    expect(result.current.hasMore).toBe(true);

    await act(async () => {
      result.current.loadMore();
    });
    await waitFor(() => expect(result.current.items.map((a) => a.id)).toEqual([1, 2, 3, 4]));
    expect(result.current.hasMore).toBe(false);
  });

  it('ignores a stale in-flight page when the ids change (race)', async () => {
    // First page hangs; a later search resolves before it does.
    let resolveStale;
    const stalePage = new Promise((res) => {
      resolveStale = res;
    });
    fetchObject.mockImplementationOnce(() => stalePage);
    fetchObject.mockImplementation((id) => Promise.resolve(art(id)));

    const { result, rerender } = renderHook(({ ids }) => usePagedArtworks(ids, 1), {
      initialProps: { ids: [1] },
    });

    // Swap to a new search before the first page settles.
    rerender({ ids: [9] });
    await waitFor(() => expect(result.current.items.map((a) => a.id)).toEqual([9]));

    // The superseded page resolves late — it must not clobber the current results.
    await act(async () => {
      resolveStale(art(1));
      await stalePage;
    });
    expect(result.current.items.map((a) => a.id)).toEqual([9]);
  });
});
