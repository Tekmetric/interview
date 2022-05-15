import React, { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';

import CharactersPagination from './CharactersPagination';
import CharactersList from './CharactersList';
import api from '../utils/api';

export default function Characters(props) {
  const [searchParams, setSearchParams] = useSearchParams();
  const pageParam = parseInt(searchParams.get('page')) || 1;

  const [characters, setCharacters] = useState(null);
  const [pageNumber, setPageNumber] = useState(pageParam);
  const [pageCount, setPageCount] = useState(0);
  const [hasError, setHasError] = useState(false);

  const onChangePage = (e, page) => {
    setSearchParams({ page });
    setPageNumber(page);
  };

  useEffect(() => {
    const onLoadPage = async () => {
      try {
        setHasError(false);
        setCharacters(null);

        const data = await api.fetchCharacters(pageNumber);
        setPageCount(data.info.pages);
        setCharacters(data.results);
      } catch (e) {
        setHasError(true);
      }
    };

    onLoadPage();
  }, [pageNumber]);

  return (
    <>
      <CharactersPagination {...{ pageNumber, pageCount, onChangePage }} />
      <CharactersList {...{ characters, hasError }} />
      <CharactersPagination {...{ pageNumber, pageCount }} />
    </>
  );
}
