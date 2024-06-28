import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';
import Sidebar from '../components/Sidebar';

const mockCategories = ['Fiction', 'Science', 'History', 'Romance', 'Mystery'];

describe('Sidebar', () => {
  it('renders categories correctly', () => {
    render(<Sidebar categories={mockCategories} selectedCategory="Fiction" onSelectCategory={jest.fn()} />);

    mockCategories.forEach((category) => {
      expect(screen.getByText(category)).toBeInTheDocument();
    });
  });

  it('calls onSelectCategory when a category is clicked', () => {
    const onSelectCategory = jest.fn();
    render(<Sidebar categories={mockCategories} selectedCategory="Fiction" onSelectCategory={onSelectCategory} />);

    fireEvent.click(screen.getByText('Science'));
    expect(onSelectCategory).toHaveBeenCalledWith('Science');
  });
});
