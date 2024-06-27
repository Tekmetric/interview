import React, { useEffect, useState, useCallback, useRef } from 'react';
import { useQuery } from 'react-query';
import InfiniteScroll from 'react-infinite-scroll-component';
import { useBooks } from '../context/BooksContext';
import BookDetailModal from './BookDetailModal';
import Loading from './Loading';
import BookListTable from './BookListTable';
import Sidebar from './Sidebar';
import { notifySuccess, notifyError } from './Notification';
import { Book } from '../types';
import { getCache, setCache } from '../utils/cache';
import { debounce } from '../utils/debounce';

const API_URL = 'https://openlibrary.org/search.json';

const fetchBooks = async (query: string, category: string, page: number) => {
  if (query) {
    // Fetch without caching for search queries
    const searchQuery = `${API_URL}?q=${query}&page=${page}`;
    const response = await fetch(searchQuery);
    if (!response.ok) throw new Error('Failed to fetch');
    const data = await response.json();
    const books: Book[] = data.docs.map((doc: any) => ({
      key: doc.key,
      title: doc.title,
      authors: doc.author_name ? doc.author_name.map((name: string) => ({ name })) : [],
      first_publish_year: doc.first_publish_year,
      description: doc.text ? doc.text.join(' ') : 'No description available',
      cover_i: doc.cover_i,
    }));
    return { books, totalBooks: data.numFound };
  } else {
    // Use cache for category results
    const cacheKey = `${category}-${page}`;
    const cachedData = getCache<{ books: Book[], totalBooks: number }>(cacheKey);
    if (cachedData) return cachedData;

    const categoryQuery = `${API_URL}?subject=${category}&page=${page}`;
    const response = await fetch(categoryQuery);
    if (!response.ok) throw new Error('Failed to fetch');

    const data = await response.json();
    const books: Book[] = data.docs.map((doc: any) => ({
      key: doc.key,
      title: doc.title,
      authors: doc.author_name ? doc.author_name.map((name: string) => ({ name })) : [],
      first_publish_year: doc.first_publish_year,
      description: doc.text ? doc.text.join(' ') : 'No description available',
      cover_i: doc.cover_i,
    }));
    setCache(cacheKey, { books, totalBooks: data.numFound });
    return { books, totalBooks: data.numFound };
  }
};

const BookTable: React.FC = () => {
  const { state, dispatch } = useBooks();
  const { books, query, page, loading, error, selectedBook } = state;
  const [selectedCategory, setSelectedCategory] = useState<string>('Fiction');
  const [categories] = useState<string[]>(['Fiction', 'Science', 'History', 'Romance', 'Mystery']);
  const scrollRef = useRef<HTMLDivElement>(null);

  // Effect to fetch books for the default category on initial load
  useEffect(() => {
    refetch();
  }, [dispatch]);

  const { data, isFetching, refetch } = useQuery(['books', query, selectedCategory, page], () =>
    fetchBooks(query, selectedCategory, page),
    {
      enabled: false, // Disable automatic refetching
      onSuccess: (data) => {
        dispatch({ type: 'FETCH_BOOKS_SUCCESS', payload: data.books });
        notifySuccess('Books fetched successfully');
      },
      onError: (error) => {
        dispatch({ type: 'FETCH_BOOKS_FAILURE', error: "Failed to fetch books!" });
        notifyError('Failed to fetch books');
      },
    }
  );

  const debouncedRefetch = useCallback(debounce(refetch, 500), [refetch]);

  const loadMoreBooks = () => {
    if (books.length < (data?.totalBooks || 0)) {
      dispatch({ type: 'SET_PAGE', payload: page + 1 });
      debouncedRefetch();
    }
  };

  const handleSearch = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const form = event.target as HTMLFormElement;
    const input = form.elements.namedItem('query') as HTMLInputElement;
    const searchTerm = input.value.trim();

    if (searchTerm) {
      dispatch({ type: 'SET_QUERY', payload: searchTerm });
      dispatch({ type: 'SET_PAGE', payload: 1 }); // Reset page to 1 when performing a search
      debouncedRefetch();
    }
  };

  const handleClearSearch = () => {
    dispatch({ type: 'SET_QUERY', payload: '' });
    setSelectedCategory('Fiction'); // Revert to default category
    dispatch({ type: 'SET_PAGE', payload: 1 }); // Reset page to 1 when clearing search
    debouncedRefetch();
  };

  const handleSelectCategory = useCallback((category: string) => {
    setSelectedCategory(category);
    dispatch({ type: 'SET_QUERY', payload: '' }); // Clear search query when selecting a category
    dispatch({ type: 'SET_PAGE', payload: 1 }); // Reset page to 1 when changing category
    debouncedRefetch();
  }, [dispatch, debouncedRefetch]);

  return (
    <div className="app-container flex">
      <Sidebar categories={categories} selectedCategory={selectedCategory} onSelectCategory={handleSelectCategory} />
      <div className="book-table-container flex-1 p-4" id="scrollableDiv" ref={scrollRef} style={{ height: '80vh', overflowY: 'auto' }}>
        <form onSubmit={handleSearch} className="mb-4">
          <input
            type="text"
            name="query"
            value={query}
            onChange={(e) => dispatch({ type: 'SET_QUERY', payload: e.target.value })}
            placeholder="Search for books"
            className="px-2 py-1 border rounded"
          />
          <button type="submit" className="ml-2 px-2 py-1 bg-blue-500 text-white rounded">
            Search
          </button>
          <button type="button" onClick={handleClearSearch} className="ml-2 px-2 py-1 bg-gray-500 text-white rounded">
            Clear
          </button>
        </form>
        {error && <div>Error: {String(error)}</div>}
        <InfiniteScroll
          dataLength={books.length}
          next={loadMoreBooks}
          hasMore={books.length < (data?.totalBooks || 0)}
          loader={<Loading />}
          className="overflow-hidden"
          scrollableTarget="scrollableDiv"
        >
          {books && books.length > 0 ? (
            <BookListTable books={books} onSelectBook={(book) => dispatch({ type: 'SET_SELECTED_BOOK', payload: book })} />
          ) : (
            <div>No books found</div>
          )}
        </InfiniteScroll>
        {loading && <Loading />}
        {selectedBook && (
          <BookDetailModal book={selectedBook} onClose={() => dispatch({ type: 'SET_SELECTED_BOOK', payload: null })} />
        )}
      </div>
    </div>
  );
};

export default BookTable;
