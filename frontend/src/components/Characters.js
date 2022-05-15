import React, { useState, useEffect, useCallback } from 'react';
import { useSearchParams } from 'react-router-dom';

import CharacterSearch from './CharacterSearch';
import CharactersPagination from './CharactersPagination';
import CharactersList from './CharactersList';
import api from '../utils/api';

export default function Characters() {
  const [searchParams, setSearchParams] = useSearchParams();
  const [query, setQuery] = useState({
    characterName: (searchParams.get('name') || '').trim(),
    pageNumber: parseInt(searchParams.get('page')) || 1,
  });
  const [characters, setCharacters] = useState(null);
  const [pageCount, setPageCount] = useState(0);
  const [characterTotal, setCharacterTotal] = useState(0);
  const [characterCount, setCharacterCount] = useState(0);
  const [hasError, setHasError] = useState(false);

  const updatePath = (q) => {
    const temp = {};
    if (q.characterName) temp.name = q.characterName;
    temp.page = q.pageNumber;
    setSearchParams(temp);
  };

  const onChangePage = useCallback(
    (e, page) => {
      const q = {
        ...query,
        pageNumber: page,
      };
      setQuery(q);
      updatePath(q);
    },
    [query],
  );

  const onChangeName = useCallback((name) => {
    const q = {
      characterName: name,
      pageNumber: 1,
    };
    setQuery(q);
    updatePath(q);
    setPageCount(0);
  }, []);

  useEffect(() => {
    const onLoadPage = async () => {
      try {
        setHasError(false);
        setCharacters(null);

        const data = await api.fetchCharacters(query.characterName, query.pageNumber);
        setPageCount(data.info.pages);
        setCharacterTotal(data.info.count);
        setCharacters(data.results);
        setCharacterCount(data.results.length);
      } catch (err) {
        setHasError(true);
      }
    };

    onLoadPage();
  }, [query.characterName, query.pageNumber]);

  const paginationProps = {
    pageNumber: query.pageNumber,
    pageCount,
    characterCount,
    characterTotal,
    onChangePage,
  };
  return (
    <>
      <CharacterSearch {...{ characterName: query.characterName, onChangeName }} />
      <CharactersPagination {...paginationProps} />
      <CharactersList {...{ characters, hasError }} />
      <CharactersPagination {...paginationProps} />
    </>
  );
}
