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
  isLoading,
}) => {
  const startIndex = (pageNumber - 1) * PAGE_SIZE + 1;
  const countLabel = `Displaying ${startIndex}-${
    startIndex + characterCount - 1
  } of ${characterTotal}`;
  return (
    <PaginationWrapper>
      <Pagination
        color='primary'
        count={pageCount}
        page={pageNumber}
        onChange={onChangePage}
        disabled={isLoading}
        showFirstButton
        showLastButton
      />
      {!!characterCount && <PaginationInfo data-testid='id-pagination-info'>{countLabel}</PaginationInfo>}
    </PaginationWrapper>
  );
};

export default React.memo(CharactersPagination, (prevProps, nextProps) => {
  if (
    prevProps.pageNumber === nextProps.pageNumber &&
    prevProps.pageCount === nextProps.pageCount &&
    prevProps.characterTotal === nextProps.characterTotal &&
    prevProps.characterCount === nextProps.characterCount &&
    prevProps.isLoading === nextProps.isLoading
  )
    return true;
  return false;
});
