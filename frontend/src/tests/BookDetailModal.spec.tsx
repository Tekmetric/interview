import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';
import BookDetailModal from '../components/BookDetailModal';
import { Book } from '../types';

const mockBook: Book = {
  key: '1',
  title: 'Test Book',
  authors: ['Author One', 'Author Two'],
  first_publish_year: '2020',
  description: 'This is a test book description.',
  cover_i: '12345',
  isbn: ['1234567890'],
  publisher: 'Test Publisher',
  number_of_pages: 300,
  subjects: ['Test Subject'],
  previewLink: 'https://books.google.com',
};

describe('BookDetailModal', () => {
  it('renders book details correctly', () => {
    render(<BookDetailModal book={mockBook} onClose={jest.fn()} />);

    expect(screen.getByText('Test Book')).toBeInTheDocument();
    expect(screen.getByText('Author One, Author Two')).toBeInTheDocument();
    expect(screen.getByText('Publisher: Test Publisher')).toBeInTheDocument();
    expect(screen.getByText('Published: 2020')).toBeInTheDocument();
    expect(screen.getByText('ISBN: 1234567890')).toBeInTheDocument();
  });

  it('calls onClose when the Close button is clicked', () => {
    const onClose = jest.fn();
    render(<BookDetailModal book={mockBook} onClose={onClose} />);

    fireEvent.click(screen.getByText('Close'));
    expect(onClose).toHaveBeenCalledTimes(1);
  });

  it('renders "Read" button with previewLink', () => {
    render(<BookDetailModal book={mockBook} onClose={jest.fn()} />);
    expect(screen.getByText('Read')).toHaveAttribute('href', 'https://books.google.com');
  });
});
