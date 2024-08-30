import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom';
import { Provider } from 'react-redux';
import { Mock } from 'vitest';
import configureStore from 'redux-mock-store';
import TaskView from './TaskView';
import {
  getCurrentPage,
  getIsLoadingTasks,
  getPages,
  getShowPagination,
  getTasks,
} from '@/store/data/task/selectors.ts';
import useGetTasks from './hooks/useGetTasks';

// Mock necessary parts
vi.mock('./hooks/useGetTasks', () => ({
  default: vi.fn(),
}));

vi.mock('@/store/data/task/selectors.ts', () => ({
  getTasks: vi.fn(),
  getPages: vi.fn(),
  getCurrentPage: vi.fn(),
  getIsLoadingTasks: vi.fn(),
  getShowPagination: vi.fn(),
}));

// Mock store setup
const mockStore = configureStore([]);
const store = mockStore({
  tasks: [],
  currentPage: 1,
  isLoadingTasks: false,
  showPagination: false,
});

describe('TaskView', () => {
  beforeEach(() => {
    (getTasks as Mock).mockReturnValue([]);
    (getPages as Mock).mockReturnValue(1);
    (getCurrentPage as Mock).mockReturnValue(1);
    (getIsLoadingTasks as Mock).mockReturnValue(false);
    (getShowPagination as Mock).mockReturnValue(false);
    (useGetTasks as Mock).mockReturnValue({
      searchValue: '',
      handleOnSearch: vi.fn(),
      handlePageChange: vi.fn(),
    });
  });

  const renderTaskView = () =>
    render(
      <Provider store={store}>
        <TaskView />
      </Provider>,
    );

  it('should display SkeletonLoading when tasks are loading', () => {
    (getIsLoadingTasks as Mock).mockReturnValue(true);
    renderTaskView();
    expect(screen.getByTestId('skeleton-loading')).toBeInTheDocument();
  });

  it('should display Pagination when there are multiple pages and tasks', () => {
    (getTasks as Mock).mockReturnValue([
      {
        id: '1',
        title: 'Task 1',
        description: 'Description 1',
        status: 'completed',
        dueDate: '19/03/2024',
        creationDate: '19/02/2024',
      },
    ]);
    (getPages as Mock).mockReturnValue(3);
    (getShowPagination as Mock).mockReturnValue(true);

    renderTaskView();

    expect(screen.getByRole('navigation')).toBeInTheDocument(); // Pagination
  });

  it('should not display Pagination when there is only one page', () => {
    (getShowPagination as Mock).mockReturnValue(false);
    renderTaskView();
    expect(screen.queryByRole('navigation')).not.toBeInTheDocument();
  });
});
