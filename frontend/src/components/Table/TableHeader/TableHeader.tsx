import React, { FC } from 'react';

import { TableHeaderContainer } from './TableHeader.styled';
import TableHeaderCell from '../TableHeaderCell/TableHeaderCell';

import { TableHeaderProps } from '../Table.types';

export const TableHeader: FC<TableHeaderProps> = ({
  headers,
  activeSorting,
  onSort,
}) => {
  return (
    <TableHeaderContainer>
      {headers.map((header) => (
        <TableHeaderCell
          key={header.key}
          id={header.key}
          width={header.size}
          value={header.label}
          isSortable={header.isSortable}
          onSort={onSort}
          activeSorting={activeSorting}
        />
      ))}
    </TableHeaderContainer>
  );
};

export default TableHeader;
