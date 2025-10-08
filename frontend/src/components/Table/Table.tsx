import React, { FC } from 'react';

import { TableContainer } from './Table.styled';
import TableHeader from './TableHeader/TableHeader';
import TableBody, { TableBodyProps } from './TableBody/TableBody';

export const HEADERS = [
  {
    label: 'Title', key: 'title', isSortable: true, size: (3 / 9) * 100,
  },
  {
    label: 'Genres', key: 'genres', isSortable: false, size: (1 / 9) * 100,
  },
  {
    label: 'Type', key: 'type', isSortable: true, size: (1 / 9) * 100,
  },
  {
    label: 'Episodes', key: 'episodes', isSortable: true, size: (1 / 9) * 100,
  },
  {
    label: 'Aired from', key: 'aired_from', isSortable: true, size: (1 / 9) * 100,
  },
  {
    label: 'Aired to', key: 'aired_to', isSortable: true, size: (1 / 9) * 100,
  },
  {
    label: 'Rating', key: 'rating', isSortable: true, size: (1 / 9) * 100,
  },
  {
    label: 'Score', key: 'score', isSortable: true, size: (1 / 9) * 100,
  },
];

type TableProps = TableBodyProps & {
  fetchNextPage: () => {};
};

export const Table: FC<TableProps> = ({ fetchNextPage, rows }) => {
  console.log('Table component rendered');
  return (
    <TableContainer>
      <TableHeader headers={HEADERS} />
      <TableBody fetchNextPage={fetchNextPage} rows={rows} />
    </TableContainer>
  );
};
