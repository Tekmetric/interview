import { render, screen, fireEvent } from '@testing-library/react';
import { Mock } from 'vitest';
import TaskHeader from './TaskHeader';
import { useMediaQuery } from '@mui/material';

// Mock the useTranslation hook
vi.mock('react-i18next', () => ({
  useTranslation: () => ({
    t: (key: string) => key,
  }),
}));

// Mock the useMediaQuery hook
vi.mock('@mui/material', async () => ({
  ...(await vi.importActual('@mui/material')),
  useMediaQuery: vi.fn(),
}));

describe('TaskHeader', () => {
  beforeEach(() => {
    (useMediaQuery as Mock).mockImplementation(query => query === '(min-width: 720px)');
  });

  it('should render the title and search', () => {
    render(<TaskHeader searchValue="123" onSearchChange={vi.fn()} />);
    expect(screen.getByTestId('search-input')).toBeInTheDocument();
    expect(screen.getByTestId('tasks-title')).toBeInTheDocument();
  });

  it('should render the search text field with the correct placeholder and label', () => {
    render(<TaskHeader searchValue="" onSearchChange={vi.fn()} />);
    const inputElement = screen.getByPlaceholderText('app.translation.header.SearchPlaceholder');
    expect(inputElement).toBeInTheDocument();
    expect(inputElement).toHaveAttribute('type', 'text');
  });

  it('should call onSearchChange when input value changes', () => {
    const handleSearchChange = vi.fn();
    render(<TaskHeader searchValue="123" onSearchChange={handleSearchChange} />);
    const inputElement = screen.getByPlaceholderText('app.translation.header.SearchPlaceholder');
    fireEvent.change(inputElement, { target: { value: 'New Task' } });
    expect(handleSearchChange).toHaveBeenCalledWith('New Task');
  });
});
