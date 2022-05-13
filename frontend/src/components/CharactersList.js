import { useState, useEffect, useCallback, useMemo } from 'react';
import { Pagination, CircularProgress } from '@mui/material';
import { useSearchParams } from "react-router-dom";

import CharacterCard from './CharacterCard';
import {
  Showcase,
  ShowcaseWrapper,
  PaginationWrapper,
} from './StyledWidgets';

import api from '../utils/api';

export default function CharactersList(props) {
  const [searchParams, setSearchParams] = useSearchParams();
  const pageParam = parseInt(searchParams.get("page")) || 1;

  const [characters, setCharacters] = useState(null);
  const [pageNumber, setPageNumber] = useState(pageParam);
  const [pageCount, setPageCount] = useState(0);
  const [hasError, setHasError] = useState(false);

  const onLoadPage = useCallback(async () => {
    try {
      setHasError(false);
      setCharacters(null);
      let data = await api.fetchCharacters(pageNumber);
      setPageCount(data.info.pages);
      setCharacters(data.results);
    } catch(e) {
      setHasError(true);
    }
  });

  const onChangePage = (e, page) => {
    setSearchParams({ page });
    setPageNumber(page);
  };

  useEffect(() => onLoadPage(), [pageNumber]);
  
  let content = null;

  content = useMemo(() => {
    if (!characters && !hasError){
      return <>
        <CircularProgress color="primary" />&nbsp;&nbsp;&nbsp;<h2>Loading characters...</h2>
      </>;
    } else if (!characters && hasError){
      return (
        <h2>We've got something suspicious. Refresh this page to try again.</h2>
      );
    } else if (characters.length === 0){
      return <h2>There's nobody in here. So sad!</h2>;
    }
    return characters.map((c) => (<CharacterCard key={c.id} data={c} />));
  }, [characters, hasError]);

  const pagination = useMemo(() => (
    <PaginationWrapper>
      <Pagination 
        color="primary"
        variant="outlined"
        size="large"
        sx={{
          '.MuiButtonBase-root': {
            fontSize: '20px',
          },
          '.Mui-selected': {
            fontWeight: 'bold',
          },
          '.MuiButtonBase-root:not(.Mui-selected)': {
            color: 'white',
          }
        }}
        count={pageCount} 
        page={pageNumber} 
        onChange={onChangePage} 
        showFirstButton 
        showLastButton
      />
    </PaginationWrapper>
  ), [pageNumber, pageCount]);

  return (
    <>
      {!hasError && pagination}
      <ShowcaseWrapper>
        <Showcase>{content}</Showcase>
      </ShowcaseWrapper>
      {!hasError && pagination}
    </>
  );
}
