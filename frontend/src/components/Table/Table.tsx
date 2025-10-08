import React, { FC } from 'react';

import { TableContainer } from './Table.styled';
import TableHeader, { TableHeaderCellItem } from './TableHeader/TableHeader';
import TableBody, { TableBodyProps } from './TableBody/TableBody';
import { ActiveSorting } from './TableHeaderCell/TableHeaderCell';

type TableProps = TableBodyProps & {
  fetchNextPage: () => {};
  isFetching: boolean;
  headers: TableHeaderCellItem[];
  activeSorting: ActiveSorting;
  onSort: (_columnId: string, _sortDirection: string) => void;
};

export const Table: FC<TableProps> = ({
  isFetching, fetchNextPage, rows, headers, activeSorting, onSort,
}) => {
  return (
    <TableContainer>
      <TableHeader activeSorting={activeSorting} onSort={onSort} headers={headers} />
      <TableBody isFetching={isFetching} fetchNextPage={fetchNextPage} rows={rows} />
    </TableContainer>
  );
};
