import { FunctionComponent } from 'react';
import { Divider, Pagination } from '@mui/material';
import { useSelector } from 'react-redux';
import {
  getCurrentPage,
  getIsLoadingTasks,
  getPages,
  getShowPagination,
  getTasks,
} from '@/store/data/task/selectors.ts';
import TaskList from './components/TaskList';
import TaskHeader from './components/TaskHeader';
import paginationStyles from './styles/paginationStyles';
import SkeletonLoading from '@/components/Skeleton';
import useGetTasks from './hooks/useGetTasks';

const TaskView: FunctionComponent = () => {
  const tasks = useSelector(getTasks);
  const pages = useSelector(getPages);
  const currentPage = useSelector(getCurrentPage);
  const isLoadingTasks = useSelector(getIsLoadingTasks);
  const showPagination = useSelector(getShowPagination);

  const { searchValue, handleOnSearch, handlePageChange } = useGetTasks(currentPage);

  return (
    <>
      <TaskHeader searchValue={searchValue} onSearchChange={handleOnSearch} />
      <Divider />
      {isLoadingTasks ? <SkeletonLoading /> : <TaskList tasks={tasks} />}
      {showPagination && tasks.length > 0 && (
        <Pagination
          disabled={isLoadingTasks}
          count={pages}
          page={currentPage}
          onChange={handlePageChange}
          sx={paginationStyles}
        />
      )}
    </>
  );
};

export default TaskView;
