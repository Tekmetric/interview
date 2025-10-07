import React, { FC } from 'react';

import { TableContainer } from './Table.styled';
import TableHeader from './TableHeader/TableHeader';
import TableBody, { TableBodyProps } from './TableBody/TableBody';

export const HEADERS = [
  {
    label: 'Title', key: 'title', isSortable: true, size: 1 / 2,
  },
  {
    label: 'Genres', key: 'genres', isSortable: false, size: 1 / 3,
  },
  {
    label: 'Type', key: 'type', isSortable: true, size: 1 / 9,
  },
  {
    label: 'Episodes', key: 'episodes', isSortable: true, size: 1 / 9,
  },
  {
    label: 'Aired from', key: 'aired_from', isSortable: true, size: 1 / 9,
  },
  {
    label: 'Aired to', key: 'aired_to', isSortable: true, size: 1 / 9,
  },
  {
    label: 'Rating', key: 'rating', isSortable: true, size: 1 / 9,
  },
  {
    label: 'Ranking', key: 'ranking', isSortable: true, size: 1 / 9,
  },
  {
    label: 'Score', key: 'score', isSortable: true, size: 1 / 9,
  },
];

type TableProps = TableBodyProps;

export const Table: FC<TableProps> = ({ rows }) => {
  console.log('Table component rendered');
  return (
    <TableContainer>
      <TableHeader headers={HEADERS} />
      <TableBody rows={rows} />
    </TableContainer>
  );
};
