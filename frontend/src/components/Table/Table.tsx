import React, { FC } from 'react';

import { TableContainer } from './Table.styled';
import TableHeader from './TableHeader/TableHeader';
import TableBody from './TableBody/TableBody';
import UtilsPage from '../UtilPages/UtilPages';

import { TableProps } from './Table.types';

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
