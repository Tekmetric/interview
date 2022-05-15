import React, { useMemo } from 'react';
import { CircularProgress } from '@mui/material';

import CharacterCard from './CharacterCard';
import { ShowcaseWrapper, Showcase } from './StyledWidgets';

const CharactersList = ({ characters, hasError }) => {
  const content = useMemo(() => {
    if (!characters && !hasError) {
      return <CircularProgress data-testid='id-loading-spinner' color='primary' />;
    } else if (!characters && hasError) {
      return <h2>We've got something suspicious. Refresh this page to try again.</h2>;
    } else if (characters.length === 0) {
      return <h2>There's nobody in here. So sad!</h2>;
    }
    return characters.map((c) => <CharacterCard key={c.id} data={c} />);
  }, [characters, hasError]);

  console.log('rendering list...');
  return (
    <ShowcaseWrapper>
      <Showcase>{content}</Showcase>
    </ShowcaseWrapper>
  );
};

export default React.memo(CharactersList);
