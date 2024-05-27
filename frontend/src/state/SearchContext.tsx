import { createContext, useState, ReactNode } from 'react';

export type SearchContextType = {
  currentPage: number;
  searchQuery: string;
  setPage: (_page: number) => void;
  setSearchQuery: (_query: string) => void;
};

export const SearchContext = createContext<SearchContextType | null>(null);

export const SearchContextProvider = ({
  children,
}: {
  children: ReactNode;
}) => {
  const [state, setState] = useState({ currentPage: 1, searchQuery: 'Batman' });

  const setPage = (pageNumber: number) => {
    setState({ ...state, currentPage: pageNumber });
  };

  const setSearch = (query: string) => {
    setState({ searchQuery: query, currentPage: 1 });
  };

  const contextValue = {
    currentPage: state.currentPage,
    searchQuery: state.searchQuery,
    setPage,
    setSearchQuery: setSearch,
  };

  return (
    <SearchContext.Provider value={contextValue}>
      {children}
    </SearchContext.Provider>
  );
};
