import React from 'react';
import { Pagination } from '@mui/material';

import { PaginationWrapper } from './StyledWidgets';

const CharactersPagination = ({ pageNumber, pageCount, onChangePage }) => {
  return (
    <PaginationWrapper>
      <Pagination
        color='primary'
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
