import { render, screen } from '@testing-library/react';
import { describe, expect, it } from 'vitest';
import Welcome from '../Welcome';

const useNavigate = vi.fn();
vi.mock('@tanstack/react-router', async () => {
  const actual = await vi.importActual('@tanstack/react-router');
  return {
    ...actual,
    useNavigate: () => useNavigate
  };
});

describe('app', () => {
  it('should render', async () => {
    render(<Welcome />);
    const h2 = screen.queryByText('Welcome to Metro Buddy!');

    expect(h2).toBeInTheDocument();
  });
});
