import React from 'react';
import { render, screen } from '@testing-library/react';
import App from './app';

test('renders learn react link', () => {
  render(<App />);
  const linkElement = screen.getByText(/Don't have an account?/i);
  expect(linkElement).toBeInTheDocument();
});
