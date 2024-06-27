import React, { createContext, useReducer, useContext, ReactNode, Dispatch } from 'react';
import { Book } from '../types';

const API_URL = 'https://openlibrary.org/search.json';

interface BookState {
  books: Book[];
  loading: boolean;
  error: any;
  query: string;
  page: number;
  selectedBook: Book | null;
}

const initialState: BookState = {
  books: [],
  loading: false,
  error: null,
  query: '',
  page: 1,
  selectedBook: null,
};

type BookAction =
  | { type: 'FETCH_BOOKS_REQUEST' }
  | { type: 'FETCH_BOOKS_SUCCESS'; payload: Book[] }
  | { type: 'FETCH_BOOKS_FAILURE'; error: any }
  | { type: 'SET_QUERY'; payload: string }
  | { type: 'SET_PAGE'; payload: number }
  | { type: 'SET_SELECTED_BOOK'; payload: Book | null };

const booksReducer = (state: BookState, action: BookAction): BookState => {
  switch (action.type) {
    case 'FETCH_BOOKS_REQUEST':
      return { ...state, loading: true, error: null };
    case 'FETCH_BOOKS_SUCCESS':
      return { ...state, loading: false, books: state.page === 1 ? action.payload : [...state.books, ...action.payload] };
    case 'FETCH_BOOKS_FAILURE':
      return { ...state, loading: false, error: action.error };
    case 'SET_QUERY':
      return { ...state, query: action.payload, page: 1, books: [] };
    case 'SET_PAGE':
      return { ...state, page: action.payload };
    case 'SET_SELECTED_BOOK':
      return { ...state, selectedBook: action.payload };
    default:
      return state;
  }
};

const BooksContext = createContext<{
  state: BookState;
  dispatch: Dispatch<BookAction>;
}>({
  state: initialState,
  dispatch: () => undefined,
});

export const BooksProvider = ({ children }: { children: ReactNode }) => {
  const [state, dispatch] = useReducer(booksReducer, initialState);

  return (
    <BooksContext.Provider value={{ state, dispatch }}>
      {children}
    </BooksContext.Provider>
  );
};

export const useBooks = () => useContext(BooksContext);
