import React from 'react';
import { render, screen } from '@testing-library/react';
import { Footer } from '../components';

test('Render Header correctly', () => {
  render(<Footer />);
  const textElement = screen.getByText(/Radu Baston/i);
  expect(textElement).toBeInTheDocument();

  const icons = screen.getByTestId('social-media-links').children;
  expect(icons.length).toBe(2);
});
