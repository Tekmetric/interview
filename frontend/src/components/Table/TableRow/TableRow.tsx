import React, { FC } from 'react';
import { format } from 'date-fns';
import { TableRowContainer } from './TableRow.styled';

export type AnimeRow = {
  title: string;
  genres: string;
  type: string;
  episodes: number;
  airedFrom: string;
  rating: string;
  airedTo: string;
  score: number;
  id: number;
};

export type TableBodyRowProps = AnimeRow;

export const TableBodyRow: FC<{ row: TableBodyRowProps }> = ({ row }) => {
  console.log('TableRow');

  return (
    <TableRowContainer onClick={() => console.log('click', row.id)}>
      <div style={{ height: '30px', width: `${(3 / 9) * 100}%` }}>{row.title}</div>
      <div style={{ height: '30px', width: `${(1 / 9) * 100}%` }}>{row.genres}</div>
      <div style={{ height: '30px', width: `${(1 / 9) * 100}%` }}>{row.type}</div>
      <div style={{ height: '30px', width: `${(1 / 9) * 100}%` }}>{row.episodes}</div>
      <div style={{ height: '30px', width: `${(1 / 9) * 100}%` }}>{format(row.airedFrom, 'yyyy/MM/dd')}</div>
      <div style={{ height: '30px', width: `${(1 / 9) * 100}%` }}>{format(row.airedTo, 'yyyy/MM/dd')}</div>
      <div style={{ height: '30px', width: `${(1 / 9) * 100}%` }}>{row.rating.split(' ')[0]}</div>
      <div style={{ height: '30px', width: `${(1 / 9) * 100}%` }}>{row.score}</div>
    </TableRowContainer>
  );
};
