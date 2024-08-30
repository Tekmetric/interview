import { render, screen } from '@testing-library/react';
import TaskList from './TaskList';
import { Task } from '@/store/data/task/types';

vi.mock('react-i18next', () => ({
  useTranslation: () => ({
    // t: (key: string) => en["translation"][key as keyof typeof en["translation"]],  // if we want to check by the key translation value
    t: (key: string) => key,
  }),
}));

describe('TaskList Component', () => {
  const mockTasks: Task[] = [
    {
      id: '1',
      title: 'Test Task 1',
      description: 'This is the first test task',
      status: 'in-progress',
      dueDate: '12/31/2024',
      creationDate: '01/01/2024',
    },
    {
      id: '2',
      title: 'Test Task 2',
      description: 'This is the second test task',
      status: 'completed',
      dueDate: '11/30/2024',
      creationDate: '02/01/2024',
    },
  ];

  it('should render the task list with the correct number of tasks', () => {
    render(<TaskList tasks={mockTasks} />);

    const taskItems = screen.getAllByTestId('tasks-list');
    expect(taskItems).toHaveLength(1);

    expect(screen.getByText('Test Task 1')).toBeInTheDocument();
    expect(screen.getByText('Test Task 2')).toBeInTheDocument();
  });

  it('should render "No results found" when the tasks array is empty', () => {
    render(<TaskList tasks={[]} />);

    // Check if the "No results found" message is displayed
    expect(screen.getByText('app.translation.tasks.NoResultsFound')).toBeInTheDocument();
  });
});
