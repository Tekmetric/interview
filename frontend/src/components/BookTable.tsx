import React, { useEffect, useState, useCallback, useRef } from 'react';
import InfiniteScroll from 'react-infinite-scroll-component';
import { useBooks } from '../context/BooksContext';
import BookDetailModal from './BookDetailModal';
import Loading from './Loading';
import BookListTable from './BookListTable';
import { notifySuccess, notifyError } from './Notification';
import { Book } from '../types';

const API_URL = 'https://openlibrary.org/search.json';

const BookTable: React.FC = () => {
  const { state, dispatch } = useBooks();
  const { books, query, page, loading, error, selectedBook } = state;
  const [totalBooks, setTotalBooks] = useState(0);
  const scrollRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const fetchBooks = async () => {
      if (query) {
        dispatch({ type: 'FETCH_BOOKS_REQUEST' });
        try {
          const response = await fetch(`${API_URL}?q=${query}&page=${page}`);
          const data = await response.json();
          const newBooks: Book[] = data.docs.map((doc: any) => ({
            key: doc.key,
            title: doc.title,
            authors: doc.author_name ? doc.author_name.map((name: string) => ({ name })) : [],
            first_publish_year: doc.first_publish_year,
            description: doc.text ? doc.text.join(' ') : 'No description available',
            cover_i: doc.cover_i
          }));
          dispatch({ type: 'FETCH_BOOKS_SUCCESS', payload: newBooks });
          setTotalBooks(data.numFound);
          notifySuccess('Books fetched successfully');
        } catch (error) {
          dispatch({ type: 'FETCH_BOOKS_FAILURE', error });
          notifyError('Failed to fetch books');
        }
      }
    };

    fetchBooks();
  }, [query, page, dispatch]);

  const loadMoreBooks = () => {
    if (books.length < totalBooks) {
      dispatch({ type: 'SET_PAGE', payload: page + 1 });
    }
  };

  const handleSearch = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const form = event.target as HTMLFormElement;
    const input = form.elements.namedItem('query') as HTMLInputElement;
    dispatch({ type: 'SET_QUERY', payload: input.value });
  };

  const handleSelectBook = useCallback((book: Book) => {
    dispatch({ type: 'SET_SELECTED_BOOK', payload: book });
  }, [dispatch]);

  return (
    <div className="p-4" id="scrollableDiv" ref={scrollRef} style={{ height: '80vh', overflowY: 'auto' }}>
      <form onSubmit={handleSearch} className="mb-4">
        <input
          type="text"
          name="query"
          defaultValue={query}
          placeholder="Search for books"
          className="px-2 py-1 border rounded"
        />
        <button type="submit" className="ml-2 px-2 py-1 bg-blue-500 text-white rounded">
          Search
        </button>
      </form>
      {error && <div>Error: {error}</div>}
      <InfiniteScroll
        dataLength={books.length}
        next={loadMoreBooks}
        hasMore={books.length < totalBooks}
        loader={<Loading />}
        className="overflow-hidden"
        scrollableTarget="scrollableDiv"
      >
        <BookListTable books={books} onSelectBook={handleSelectBook} />
      </InfiniteScroll>
      {loading && <Loading />}
      {selectedBook && (
        <BookDetailModal book={selectedBook} onClose={() => dispatch({ type: 'SET_SELECTED_BOOK', payload: null })} />
      )}
    </div>
  );
};

export default BookTable;
