import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';
import { QueryClient, QueryClientProvider } from 'react-query';
import BookTable from '../components/BookTable';
import { BooksProvider } from '../context/BooksContext';

const queryClient = new QueryClient();

describe('BookTable', () => {
  it('renders BookTable correctly', () => {
    render(
      <BooksProvider>
        <QueryClientProvider client={queryClient}>
          <BookTable />
        </QueryClientProvider>
      </BooksProvider>
    );

    expect(screen.getByPlaceholderText('Search for books and hit enter to see results')).toBeInTheDocument();
  });

  it('performs a search when the form is submitted', () => {
    render(
      <BooksProvider>
        <QueryClientProvider client={queryClient}>
          <BookTable />
        </QueryClientProvider>
      </BooksProvider>
    );

    fireEvent.change(screen.getByPlaceholderText('Search for books and hit enter to see results'), { target: { value: 'test' } });
    fireEvent.submit(screen.getByRole('form'));

    // Check if the search term is updated
    expect(screen.getByDisplayValue('test')).toBeInTheDocument();
  });
});
