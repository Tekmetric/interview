import React, { FC } from 'react';

import { TableContainer } from './Table.styled';
import TableHeader, { TableHeaderCellItem } from './TableHeader/TableHeader';
import TableBody, { TableBodyProps } from './TableBody/TableBody';
import { ActiveSorting } from './TableHeaderCell/TableHeaderCell';
import UtilsPage from '../UtilPages/UtilPages';

type TableProps = TableBodyProps & {
  fetchNextPage: () => {};
  isFetching: boolean;
  headers: TableHeaderCellItem[];
  activeSorting: ActiveSorting;
  onSort: (_columnId: string, _sortDirection: string) => void;
  hasError?: boolean
};

export const Table: FC<TableProps> = ({
  isFetching, fetchNextPage, rows, headers, activeSorting, onSort, hasError,
}) => {
  return (
    <TableContainer>
      <TableHeader activeSorting={activeSorting} onSort={onSort} headers={headers} />
      {hasError && <UtilsPage type="ERROR" />}
      {(!rows || !rows.length) && <UtilsPage type="NOT_FOUND" />}
      {!hasError && rows.length && <TableBody isFetching={isFetching} fetchNextPage={fetchNextPage} rows={rows} />}
    </TableContainer>
  );
};
