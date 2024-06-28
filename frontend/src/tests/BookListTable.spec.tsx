import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';
import BookListTable from '../components/BookListTable';
import { Book } from '../types';

const mockBooks: Book[] = [
  {
    key: '1',
    title: 'Test Book 1',
    authors: ['Author One'],
    first_publish_year: '2020',
    description: 'This is a test book description.',
    cover_i: '12345',
    isbn: ['1234567890'],
    publisher: 'Test Publisher',
    number_of_pages: 300,
    subjects: ['Test Subject'],
    previewLink: 'https://books.google.com',
  },
  {
    key: '2',
    title: 'Test Book 2',
    authors: ['Author Two'],
    first_publish_year: '2021',
    description: 'This is another test book description.',
    cover_i: '12346',
    isbn: ['1234567891'],
    publisher: 'Another Publisher',
    number_of_pages: 400,
    subjects: ['Another Subject'],
    previewLink: 'https://books.google.com',
  },
];

describe('BookListTable', () => {
  it('renders book list correctly', () => {
    render(<BookListTable books={mockBooks} onSelectBook={jest.fn()} />);

    expect(screen.getByText('Test Book 1')).toBeInTheDocument();
    expect(screen.getByText('Test Book 2')).toBeInTheDocument();
  });

  it('calls onSelectBook when "Details" is clicked', () => {
    const onSelectBook = jest.fn();
    render(<BookListTable books={mockBooks} onSelectBook={onSelectBook} />);

    fireEvent.click(screen.getAllByText('Details')[0]);
    expect(onSelectBook).toHaveBeenCalledWith(mockBooks[0]);
  });
});
