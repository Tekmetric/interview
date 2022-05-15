import React from 'react';
import { Pagination } from '@mui/material';

import { PaginationWrapper, PaginationInfo } from './StyledWidgets';

const PAGE_SIZE = 20;
const CharactersPagination = ({
  pageNumber,
  pageCount,
  characterCount,
  characterTotal,
  onChangePage,
}) => {
  const startIndex = (pageNumber - 1) * PAGE_SIZE + 1;
  const countLabel = characterCount
    ? `Displaying ${startIndex}-${startIndex + characterCount - 1} of ${characterTotal}`
    : '';
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
      <PaginationInfo data-testid='id-pagination-info'>{countLabel}</PaginationInfo>
    </PaginationWrapper>
  );
};

export default React.memo(CharactersPagination);
