import React, { useState, useEffect, useCallback } from 'react';
import { useSearchParams } from 'react-router-dom';

import CharacterSearch from './CharacterSearch';
import CharactersPagination from './CharactersPagination';
import CharactersWrapper from './CharactersWrapper';
import { useFetchCharacters } from '../hooks';

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
  const { isLoading, hasError, fetchCharacters } = useFetchCharacters();

  const updatePath = () => {
    const temp = {};
    if (options.characterName) temp.name = options.characterName;
    temp.page = options.pageNumber;
    setSearchParams(temp);
  };

  const onChangePage = (e, page) => {
    setOptions({ ...options, pageNumber: page });
  };

  const onChangeName = useCallback((name) => {
    setOptions({ ...initialOptions, characterName: name, pageNumber: 1 });
  }, []);

  useEffect(() => {
    const onLoadPage = async () => {
      updatePath();

      const data = await fetchCharacters(options.characterName, options.pageNumber);
      if (!data) return;

      setCharacters(data.characters);
      delete data.characters;
      setOptions((prevOptions) => ({
        ...prevOptions,
        ...data,
      }));
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
      <CharactersWrapper {...{ characters, hasError, isLoading }} />
      <CharactersPagination {...paginationProps} />
    </>
  );
}
