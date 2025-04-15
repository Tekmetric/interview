import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import ToolBar from './ToolBar';

describe('Toolbar', () => {
  const mockHandleSearch = vi.fn();

  it('renders correctly', () => {
    const { container } = render(
      <MemoryRouter>
        <ToolBar handleSearch={mockHandleSearch} currentSearch="My Search" resultCount={5} />
      </MemoryRouter>
    );
    expect(container).toMatchSnapshot();
  });

  it('render correctly with search and results', () => {
    const { container } = render(
      <MemoryRouter>
        <ToolBar handleSearch={mockHandleSearch} currentSearch="My Search" resultCount={5} />
      </MemoryRouter>
    );

    expect(screen.getByTestId('search')).toHaveValue('My Search');
    expect(screen.getByText('5 search result found')).toBeInTheDocument();
  });
});
