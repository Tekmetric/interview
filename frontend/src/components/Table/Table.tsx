import React, { FC } from 'react';

import { TableContainer } from './Table.styled';
import TableHeader, { TableHeaderCell } from './TableHeader/TableHeader';
import TableBody, { TableBodyProps } from './TableBody/TableBody';

type TableProps = TableBodyProps & {
  fetchNextPage: () => {};
  isFetching: boolean;
  headers: TableHeaderCell[];
};

export const Table: FC<TableProps> = ({
  isFetching, fetchNextPage, rows, headers,
}) => {
  return (
    <TableContainer>
      <TableHeader headers={headers} />
      <TableBody isFetching={isFetching} fetchNextPage={fetchNextPage} rows={rows} />
    </TableContainer>
  );
};
