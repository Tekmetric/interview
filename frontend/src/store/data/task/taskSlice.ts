import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { TaskPayload, TaskState } from './types';
import { fetchTasks } from './thunks';

export const initialState: TaskState = {
  list: {
    data: [],
    count: 10,
    currentPage: 1,
    totalPages: 0,
    firstPage: 1,
    lastPage: 0,
    nextPage: 0,
    prevPage: 0,
  },
  isLoading: false,
};

const taskSlice = createSlice({
  name: 'task',
  initialState,
  reducers: {},
  extraReducers: builder => {
    builder
      .addCase(fetchTasks.pending, state => {
        state.isLoading = true;
      })
      .addCase(fetchTasks.fulfilled, (state, action: PayloadAction<TaskPayload>) => {
        state.isLoading = false;
        state.list = action.payload;
      })
      .addCase(fetchTasks.rejected, (state, action) => {
        console.log('error: ', action.error.message);
        // handle error case message
        state.isLoading = false;
      });
  },
});

export default taskSlice.reducer;
