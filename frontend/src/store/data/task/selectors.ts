import { State } from '../../types.ts';
import { Task } from './types.ts';

export const getTasks = (state: State): Task[] => state.data.tasks.list.data;
export const getIsLoadingTasks = (state: State): boolean => state.data.tasks.isLoading;

export const getPages = (state: State): number => state.data.tasks.list.totalPages;

export const getCurrentPage = (state: State): number => state.data.tasks.list.currentPage;

export const getShowPagination = (state: State): boolean =>
  state.data.tasks.list.firstPage < state.data.tasks.list.lastPage;
