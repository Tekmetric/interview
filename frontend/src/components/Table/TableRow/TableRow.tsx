import React, { FC } from 'react';
import { useNavigate } from 'react-router';
import { TableRowContainer } from './TableRow.styled';
import { AdditionalDetails } from '../../../types';
import TableCell from '../TableCell/TableCell';

export type AnimeRow = {
  title: string;
  genres: AdditionalDetails[];
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
  const navigate = useNavigate();
  return (
    <TableRowContainer onClick={() => navigate(`/anime/${row.id}`)}>
      <TableCell value={row.title} width={(3 / 9) * 100} />
      <TableCell value={row.genres} width={(1 / 9) * 100} type="genre" />
      <TableCell value={row.type} width={(1 / 9) * 100} />
      <TableCell value={row.episodes} width={(1 / 9) * 100} />
      <TableCell value={row.airedFrom} width={(1 / 9) * 100} type="date" />
      <TableCell value={row.airedTo} width={(1 / 9) * 100} type="date" />
      <TableCell value={row.rating} width={(1 / 9) * 100} type="rating" />
      <TableCell value={row.score} width={(1 / 9) * 100} />
    </TableRowContainer>
  );
};
