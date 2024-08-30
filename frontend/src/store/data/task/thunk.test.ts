import { describe, it, expect, vi } from 'vitest';
import { configureStore } from '@reduxjs/toolkit';
import { fetchTasks } from '@/store/data/task/thunks';
import get from '@/api/get';
import taskSlice from './taskSlice';

vi.mock('@/api/get', () => ({
  __esModule: true,
  default: vi.fn(),
}));

const mockStore = configureStore({
  reducer: {
    data: taskSlice,
  },
});

describe('fetchTasks thunk', () => {
  it('should handle successful fetch', async () => {
    const mockTasks = {
      first: 1,
      prev: null,
      next: null,
      last: 1,
      pages: 1,
      items: 1,
      data: [
        {
          id: '1',
          title: 'Mock title',
          description: 'Mock description',
          status: 'in-progress',
          dueDate: '02/09/2024',
          creationDate: '01/09/2024',
        },
      ],
    };

    const mockData = {
      data: [
        {
          id: '1',
          title: 'Mock title',
          description: 'Mock description',
          status: 'in-progress',
          dueDate: '09-01-2024',
          creationDate: '09-01-2024',
        },
      ],
      count: 1,
      totalPages: 1,
      currentPage: 1,
      firstPage: 1,
      lastPage: 1,
      nextPage: null,
      prevPage: null,
    };
    vi.mocked(get).mockResolvedValue(mockTasks);

    const store = mockStore;

    const resultAction = await store.dispatch(fetchTasks({ page: 1 }));

    expect(fetchTasks.fulfilled.match(resultAction)).toBe(true);
    expect(store.getState().data.list).toEqual(mockData);
  });

  it('should handle fetch error', async () => {
    const error = new Error('Network Error');

    vi.mocked(get).mockRejectedValue(error);

    const store = mockStore;

    const resultAction = await store.dispatch(fetchTasks({ page: 1 }));

    expect(fetchTasks.rejected.match(resultAction)).toBe(true);
    expect(store.getState().data.isLoading).toBe(false);
  });
});
