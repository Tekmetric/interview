import React from 'react';
import { Pagination } from '@mui/material';

import { PaginationWrapper } from './StyledWidgets';

const CharactersPagination = ({ pageNumber, pageCount, onChangePage }) => {
  return (
    <PaginationWrapper>
      <Pagination
        color='primary'
        variant='outlined'
        size='large'
        sx={{
          '.MuiButtonBase-root': {
            fontSize: '20px',
          },
          '.Mui-selected': {
            fontWeight: 'bold',
          },
          '.MuiButtonBase-root:not(.Mui-selected)': {
            color: 'white',
          },
        }}
        count={pageCount}
        page={pageNumber}
        onChange={onChangePage}
        showFirstButton
        showLastButton
      />
    </PaginationWrapper>
  );
};

export default React.memo(CharactersPagination);
