import { useState, useEffect, useCallback, useMemo } from 'react';
import { Pagination, CircularProgress } from '@mui/material';

import CharacterCard from './components/CharacterCard';
import {
  AppHeading,
  Showcase,
  ShowcaseWrapper,
  PaginationWrapper,
} from './components/StyledWidgets';

import api from './utils/api';

export default function App() {
  const [characters, setCharacters] = useState(null);
  const [pageNumber, setPageNumber] = useState(1);
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
    return characters.map((c) => (<CharacterCard data={c} />));
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
      <AppHeading>
        <h1>The Rick and Morty</h1>
        <h3>(All Characters)</h3>
      </AppHeading>
      {!hasError && pagination}
      <ShowcaseWrapper>
        <Showcase>{content}</Showcase>
      </ShowcaseWrapper>
      {!hasError && pagination}
    </>
  );
}
