import { ChangeEvent, useCallback, useEffect, useMemo, useState } from 'react';
import { fetchTasks, FetchTasksParams } from '@/store/data/task/thunks.ts';
import { debounce } from '@mui/material';
import { useDispatch } from 'react-redux';
import { Action, ThunkDispatch } from '@reduxjs/toolkit';
import { TaskPayload } from '@/store/data/task/types.ts';

const useGetTasks = (currentPage: number) => {
  const dispatch = useDispatch<ThunkDispatch<TaskPayload, FetchTasksParams, Action>>();
  const [searchValue, setSearchValue] = useState<string>('');

  useEffect(() => {
    dispatch(fetchTasks({ page: currentPage }));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [dispatch]);

  const debouncedFn = useMemo(
    () =>
      debounce((value: string) => {
        dispatch(fetchTasks({ page: currentPage, title: value }));
      }, 300),
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [dispatch],
  );

  const handleOnSearch = useCallback(
    (value: string) => {
      setSearchValue(value);
      debouncedFn(value);
    },
    [debouncedFn],
  );

  const handlePageChange = (_event: ChangeEvent<unknown>, page: number) => dispatch(fetchTasks({ page }));

  return {
    searchValue,
    handleOnSearch,
    handlePageChange,
  };
};

export default useGetTasks;
