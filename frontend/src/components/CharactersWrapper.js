import React from 'react';
import { CircularProgress } from '@mui/material';

import CharacterCard from './CharacterCard';
import { ShowcaseWrapper, Showcase } from './StyledWidgets';

const CharactersList = ({ data }) => {
  return data.map((c) => <CharacterCard key={c.id} data={c} />);
};

const CharactersWrapper = ({ characters, hasError, isLoading }) => {
  const getContent = () => {
    if (hasError) {
      return <h2>We've got something suspicious. Refresh this page to try again.</h2>;
    } else if (isLoading) {
      return <CircularProgress data-testid='id-loading-spinner' color='primary' />;
    } else if (characters && characters.length === 0) {
      return <h2 data-testid='id-message-nobody'>There's nobody in here. So sad!</h2>;
    } else if (characters) {
      return <CharactersList data={characters} />;
    }
    return <nobr />;
  };

  return (
    <ShowcaseWrapper>
      <Showcase>{getContent()}</Showcase>
    </ShowcaseWrapper>
  );
};

export default React.memo(CharactersWrapper);
