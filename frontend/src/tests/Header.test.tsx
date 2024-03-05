import React from 'react';
import { render, screen } from '@testing-library/react';
import { Header } from '../components';

test('Render Header correctly', () => {
  render(<Header />);
  const textElement = screen.getByText(/Tekmetric Movie Plot/i);
  expect(textElement).toBeInTheDocument();

  const subtitleElement = screen.getByText(
    /Famous movies described with random quotes from the Internet/i,
  );
  expect(subtitleElement).toBeInTheDocument();
});
