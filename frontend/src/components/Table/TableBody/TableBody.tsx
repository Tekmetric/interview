import React, { FC } from 'react';

import { TableBodyContainer } from './TableBody.styled';

type TableBodyRow = {
  title: string;
  genres: string;
  type: string;
  episodes: number;
  airedFrom: string;
  rating: string;
  airedTo: string;
  ranking: number;
  score: number;
};

export type TableBodyProps = {
  rows: TableBodyRow[];
};

export const TableBody: FC<TableBodyProps> = ({ rows }) => {
  console.log('TableBody');
  return (
    <TableBodyContainer>
      {rows.map((row) => (
        <tr key={row.title}>
          <td style={{ height: '30px', width: `${1 / 2}%` }}>{row.title}</td>
          <td style={{ height: '30px', width: `${1 / 3}%` }}>{row.genres}</td>
          <td style={{ height: '30px', width: `${1 / 9}%` }}>{row.type}</td>
          <td style={{ height: '30px', width: `${1 / 9}%` }}>{row.episodes}</td>
          <td style={{ height: '30px', width: `${1 / 9}%` }}>{row.airedFrom}</td>
          <td style={{ height: '30px', width: `${1 / 9}%` }}>{row.airedTo}</td>
          <td style={{ height: '30px', width: `${1 / 9}%` }}>{row.rating}</td>
          <td style={{ height: '30px', width: `${1 / 9}%` }}>{row.ranking}</td>
          <td style={{ height: '30px', width: `${1 / 9}%` }}>{row.score}</td>
          {/* <td style={{ height: '30px', width: `${1 / 2}%`, textAlign: 'center' }}>{row.title}</td>
          <td style={{ height: '30px', width: `${1 / 3}%`, textAlign: 'center' }}>{row.genres}</td>
          <td style={{ height: '30px', width: `${1 / 9}%`, textAlign: 'center' }}>{row.type}</td>
          <td style={{ height: '30px', width: `${1 / 9}%`, textAlign: 'center' }}>{row.episodes}</td>
          <td style={{ height: '30px', width: `${1 / 9}%`, textAlign: 'center' }}>{row.airedFrom}</td>
          <td style={{ height: '30px', width: `${1 / 9}%`, textAlign: 'center' }}>{row.airedTo}</td>
          <td style={{ height: '30px', width: `${1 / 9}%`, textAlign: 'center' }}>{row.rating}</td>
          <td style={{ height: '30px', width: `${1 / 9}%`, textAlign: 'center' }}>{row.ranking}</td>
          <td style={{ height: '30px', width: `${1 / 9}%`, textAlign: 'center' }}>{row.score}</td> */}
        </tr>
      ))}
    </TableBodyContainer>
  );
};

export default TableBody;
