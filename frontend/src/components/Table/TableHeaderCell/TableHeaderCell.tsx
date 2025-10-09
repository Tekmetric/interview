import React, { FC } from 'react';
import SwapVertIcon from '@mui/icons-material/SwapVert';
import ArrowDownwardIcon from '@mui/icons-material/ArrowDownward';
import ArrowUpwardIcon from '@mui/icons-material/ArrowUpward';

import { TableHeaderCellContainer } from './TableHeaderCell.styled';
import { TableHeaderCellProps } from '../Table.types';

export const TableHeaderCell: FC<TableHeaderCellProps> = ({
  id, value, width, onSort, activeSorting, isSortable,
}) => {
  if (!isSortable) {
    return <TableHeaderCellContainer $width={width}>{value}</TableHeaderCellContainer>;
  }

  const getIcon = () => {
    if (activeSorting?.columnId !== id) {
      return <SwapVertIcon onClick={() => onSort(id, 'asc')} />;
    }

    if (activeSorting.sortDirection === 'desc') {
      return <ArrowDownwardIcon onClick={() => onSort(id, 'asc')} />;
    }

    return <ArrowUpwardIcon onClick={() => onSort(id, 'desc')} />;
  };

  return (
    <TableHeaderCellContainer $width={width} $active={activeSorting?.columnId === id}>
      {value}
      {getIcon()}
    </TableHeaderCellContainer>
  );
};

export default TableHeaderCell;
