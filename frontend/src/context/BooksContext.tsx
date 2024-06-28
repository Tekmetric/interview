import React, { createContext, useReducer, useContext, ReactNode } from 'react';
import { Book } from '../types';

interface BooksState {
  books: Book[];
  query: string;
  page: number;
  loading: boolean;
  error: string | null;
  selectedBook: Book | null;
}

interface BooksAction {
  type: string;
  payload?: any;
  error?: string;
}

const initialState: BooksState = {
  books: [],
  query: '',
  page: 1,
  loading: false,
  error: null,
  selectedBook: null,
};

const BooksContext = createContext<{ state: BooksState; dispatch: React.Dispatch<BooksAction> }>({
  state: initialState,
  dispatch: () => null,
});

const booksReducer = (state: BooksState, action: BooksAction): BooksState => {
  switch (action.type) {
    case 'FETCH_BOOKS_REQUEST':
      return { ...state, loading: true, error: null };
    case 'FETCH_BOOKS_SUCCESS':
      return { ...state, loading: false, books: state.page === 1 ? action.payload : [...state.books, ...action.payload] };
    case 'FETCH_BOOKS_FAILURE':
      return { ...state, loading: false, error: action.error! };
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

export const BooksProvider = ({ children }: { children: ReactNode }) => {
  const [state, dispatch] = useReducer(booksReducer, initialState);
  return (
    <BooksContext.Provider value={{ state, dispatch }}>
      {children}
    </BooksContext.Provider>
  );
};

export const useBooks = () => useContext(BooksContext);
