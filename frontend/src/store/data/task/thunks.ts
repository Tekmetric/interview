import { createAsyncThunk } from '@reduxjs/toolkit';
import get from '@/api/get.ts';
import { mapTasks } from '@/store/data/task/utils/mapTasks.ts';
import { TaskPayload } from '@/store/data/task/types.ts';

const BASE_URL = 'http://localhost:4000/tasks';

export interface FetchTasksParams {
  page: number;
  title?: string;
}

const fetchTasks = createAsyncThunk<TaskPayload, FetchTasksParams>(
  'task/fetchTasks',
  async ({ page, title }: FetchTasksParams, { rejectWithValue }) => {
    const queryParams = new URLSearchParams({ _page: page.toString() });
    if (title) {
      // search should cover "title" OR "description" fields but the "json-server" API doesn't support "OR" operator (only "AND"),
      // also it doesn't work as expected with "title_like", it only works with exact match "title" and is CASE-SENSITIVE :(
      queryParams.append('title', title);
    }
    const url = `${BASE_URL}?${queryParams.toString()}`;
    try {
      const data = await get(url);
      return mapTasks(data);
    } catch (err) {
      return rejectWithValue(err);
    }
  },
);

export { fetchTasks };
