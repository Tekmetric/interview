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
import { FiSearch, FiX } from 'react-icons/fi';

const API_URL = 'https://openlibrary.org/search.json';

const fetchBooks = async (query: string, category: string, page: number) => {
  const limit = 20;
  if (query) {
    const searchQuery = `${API_URL}?q=${query}&page=${page}&limit=${limit}`;
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
    const cacheKey = `${category}-${page}`;
    const cachedData = getCache<{ books: Book[], totalBooks: number }>(cacheKey);
    if (cachedData) return cachedData;

    const categoryQuery = `${API_URL}?subject=${category}&page=${page}&limit=${limit}`;
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
      <div className="flex-1 p-4 bg-veryVeryLightGray">
        <div className="sticky top-0 bg-veryVeryLightGray z-10">
          <form onSubmit={handleSearch} className="mb-4 relative">
            <input
              type="text"
              name="query"
              value={query}
              onChange={(e) => dispatch({ type: 'SET_QUERY', payload: e.target.value })}
              placeholder="Search for books"
              className="px-4 py-2 border rounded w-full pl-10 pr-10"
            />
            {query ? (
              <button type="button" onClick={handleClearSearch} className="absolute right-2 top-2 text-gray-500 hover:text-gray-700">
                <FiX />
              </button>
            ) : (
              <button type="submit" className="absolute right-2 top-2 text-gray-500 hover:text-gray-700">
                <FiSearch />
              </button>
            )}
          </form>
        </div>
        <div className="book-table-container relative" id="scrollableDiv" ref={scrollRef} style={{ height: '80vh', overflowY: 'auto' }}>
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
            ) : (isFetching ? (<Loading /> ):
              (<div>No books found</div>)
            )}
            {books?.length > 0 && isFetching && (<Loading />)}
          </InfiniteScroll>
          {selectedBook && (
            <BookDetailModal book={selectedBook} onClose={() => dispatch({ type: 'SET_SELECTED_BOOK', payload: null })} />
          )}
        </div>
      </div>
    </div>
  );
};

export default BookTable;
