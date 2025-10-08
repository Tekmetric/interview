import React, { FC } from 'react';
import { useNavigate } from 'react-router';
import { format } from 'date-fns';
import { TableRowContainer } from './TableRow.styled';
import { AdditionalDetails } from '../../../types';

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

// Function to extract genre names from the given object
export const extractGenreNames = (genres: AdditionalDetails[]): string[] => {
  return genres.map((genre) => genre.name);
};

export const TableBodyRow: FC<{ row: TableBodyRowProps }> = ({ row }) => {
  const navigate = useNavigate();
  return (
    <TableRowContainer onClick={() => navigate(`/anime/${row.id}`)}>
      <div style={{ height: '30px', width: `${(3 / 9) * 100}%` }}>{row.title}</div>
      <div style={{ height: '30px', width: `${(1 / 9) * 100}%` }}>{extractGenreNames(row.genres)[0]}</div>
      <div style={{ height: '30px', width: `${(1 / 9) * 100}%` }}>{row.type}</div>
      <div style={{ height: '30px', width: `${(1 / 9) * 100}%` }}>{row.episodes}</div>
      <div style={{ height: '30px', width: `${(1 / 9) * 100}%` }}>{format(row.airedFrom, 'yyyy/MM/dd')}</div>
      <div style={{ height: '30px', width: `${(1 / 9) * 100}%` }}>{format(row.airedTo, 'yyyy/MM/dd')}</div>
      <div style={{ height: '30px', width: `${(1 / 9) * 100}%` }}>{row.rating.split(' ')[0]}</div>
      <div style={{ height: '30px', width: `${(1 / 9) * 100}%` }}>{row.score}</div>
    </TableRowContainer>
  );
};
