import React, { FC } from 'react';

import { TableContainer } from './Table.styled';
import TableHeader, { TableHeaderCellItem } from './TableHeader/TableHeader';
import TableBody, { TableBodyProps } from './TableBody/TableBody';
import { ActiveSorting } from './TableHeaderCell/TableHeaderCell';
import ErrorPage from '../ErrorPage/ErrorPage';
import EmptyPage from '../EmptyPage/EmptyPage';

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
      {hasError && <ErrorPage />}
      {(!rows || !rows.length) && <EmptyPage /> }
      {!hasError && rows.length && <TableBody isFetching={isFetching} fetchNextPage={fetchNextPage} rows={rows} />}
    </TableContainer>
  );
};
