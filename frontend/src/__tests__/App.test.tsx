import { render, screen } from '@testing-library/react';
import { describe, expect, it } from 'vitest';
import App from '../App';

describe('app', () => {
  it('should render', async () => {
    render(<App />);
    const h2 = screen.queryByText('Welcome to the interview app!');

    expect(h2).toBeInTheDocument();
  });
});
