import { ChangeEvent } from 'react';
import { renderHook, act } from '@testing-library/react-hooks';
import { useDispatch } from 'react-redux';
import { fetchTasks } from '@/store/data/task/thunks.ts';
import useGetTasks from './useGetTasks';
import { vi } from 'vitest';

vi.mock('react-redux', () => ({
  useDispatch: vi.fn(),
}));

vi.mock('@/store/data/task/thunks', () => ({
  fetchTasks: vi.fn(),
}));

describe('useGetTasks', () => {
  const mockDispatch = vi.fn();
  const mockFetchTasks = fetchTasks;

  beforeEach(() => {
    // @ts-expect-error: Mocked useDispatch does not match the original type
    useDispatch.mockReturnValue(mockDispatch);
    mockDispatch.mockClear();
    // @ts-expect-error: Mocked fetchTasks does not match the original type
    mockFetchTasks.mockClear();
  });

  it('should fetch tasks on initial render with current page', () => {
    renderHook(() => useGetTasks(1));

    expect(mockDispatch).toHaveBeenCalledTimes(1);
    expect(mockFetchTasks).toHaveBeenCalledWith({ page: 1 });
  });

  it('should debounce and fetch tasks on search input', async () => {
    const { result } = renderHook(() => useGetTasks(1));

    act(() => {
      result.current.handleOnSearch('test');
    });

    expect(result.current.searchValue).toBe('test');

    await act(async () => {
      await new Promise(resolve => setTimeout(resolve, 350));
    });

    expect(mockDispatch).toHaveBeenCalledTimes(2);
    expect(mockFetchTasks).toHaveBeenCalledWith({ page: 1, title: 'test' });
  });

  it('should fetch tasks on page change', () => {
    const { result } = renderHook(() => useGetTasks(1));

    act(() => {
      result.current.handlePageChange({} as ChangeEvent<unknown>, 2);
    });

    expect(mockDispatch).toHaveBeenCalledTimes(2);
    expect(mockFetchTasks).toHaveBeenCalledWith({ page: 2 });
  });
});
