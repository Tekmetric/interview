import React, { FC } from 'react';

import { TableHeaderContainer } from './TableHeader.styled';
import TableHeaderCell, { ActiveSorting } from '../TableHeaderCell/TableHeaderCell';

export type TableHeaderCellItem = {
  label: string;
  key: string;
  isSortable: boolean;
  sortDirection?: 'asc' | 'desc';
  size: number;
};

type TableHeaderProps = {
  headers: TableHeaderCellItem[];
  activeSorting: ActiveSorting;
  onSort: (_columnId: string, _sortDirection: string) => void;
};

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
