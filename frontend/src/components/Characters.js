import React, { useState, useEffect, useCallback } from 'react';
import { useSearchParams } from 'react-router-dom';

import CharacterSearch from './CharacterSearch';
import CharactersPagination from './CharactersPagination';
import CharactersList from './CharactersList';
import api from '../utils/api';

const initialOptions = {
  pageCount: 0,
  characterTotal: 0,
  characterCount: 0,
};

export default function Characters() {
  const [searchParams, setSearchParams] = useSearchParams();
  const [options, setOptions] = useState({
    characterName: (searchParams.get('name') || '').trim(),
    pageNumber: parseInt(searchParams.get('page')) || 1,
    ...initialOptions,
  });
  const [characters, setCharacters] = useState(null);
  const [hasError, setHasError] = useState(false);

  const updatePath = () => {
    const temp = {};
    if (options.characterName) temp.name = options.characterName;
    temp.page = options.pageNumber;
    setSearchParams(temp);
  };

  const onChangePage = useCallback(
    (e, page) => {
      setOptions({ ...options, pageNumber: page });
    },
    [options],
  );

  const onChangeName = useCallback((name) => {
    setOptions({ ...initialOptions, characterName: name, pageNumber: 1 });
  }, []);

  useEffect(() => {
    const onLoadPage = async () => {
      try {
        setHasError(false);
        setCharacters(null);
        updatePath();

        const data = await api.fetchCharacters(options.characterName, options.pageNumber);
        setOptions((prevOptions) => ({
          ...prevOptions,
          pageCount: data.info.pages,
          characterTotal: data.info.count,
          characterCount: data.results.length,
        }));
        setCharacters(data.results);
      } catch (err) {
        setHasError(true);
      }
    };

    onLoadPage();
  }, [options.characterName, options.pageNumber]);

  const paginationProps = {
    ...options,
    onChangePage,
  };
  return (
    <>
      <CharacterSearch {...{ characterName: options.characterName, onChangeName }} />
      <CharactersPagination {...paginationProps} />
      <CharactersList {...{ characters, hasError }} />
      <CharactersPagination {...paginationProps} />
    </>
  );
}
