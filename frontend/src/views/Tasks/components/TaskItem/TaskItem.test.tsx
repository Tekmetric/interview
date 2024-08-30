import { render, screen } from '@testing-library/react';
import TaskItem from './TaskItem';
import { Task } from '@/store/data/task/types';

vi.mock('react-i18next', () => ({
  useTranslation: () => ({
    t: (key: string) => key,
  }),
}));

describe('TaskItem Component', () => {
  const mockTask: Task = {
    id: '1',
    title: 'Test Task',
    description: 'This is a test task',
    status: 'in-progress',
    dueDate: '12/31/2024',
    creationDate: '01/01/2024',
  };

  it('should render the TaskItem component & send the correct props to TaskFields', () => {
    render(<TaskItem task={mockTask} />);

    const titleElement = screen.getByTestId('task-id-1');
    expect(titleElement).toBeInTheDocument();

    const descriptionElement = screen.getByText(/This is a test task/i);
    expect(descriptionElement).toBeInTheDocument();

    const statusElement = screen.getByText('app.translation.task.status.InProgress');
    expect(statusElement).toBeInTheDocument();

    const dueDateElement = screen.getByText(/12\/31\/2024/i);
    expect(dueDateElement).toBeInTheDocument();
  });
});
