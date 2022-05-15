import React from 'react';
import { CircularProgress } from '@mui/material';

import CharacterCard from './CharacterCard';
import { ShowcaseWrapper, Showcase } from './StyledWidgets';

const CharactersList = ({ characters, hasError }) => {
  const getContent = () => {
    if (!hasError && !characters) {
      return <CircularProgress data-testid='id-loading-spinner' color='primary' />;
    } else if (!hasError && characters.length === 0) {
      return <h2>There's nobody in here. So sad!</h2>;
    } else if (hasError) {
      return <h2>We've got something suspicious. Refresh this page to try again.</h2>;
    }
    return characters.map((c) => <CharacterCard key={c.id} data={c} />);
  };

  return (
    <ShowcaseWrapper>
      <Showcase>{getContent()}</Showcase>
    </ShowcaseWrapper>
  );
};

export default React.memo(CharactersList);