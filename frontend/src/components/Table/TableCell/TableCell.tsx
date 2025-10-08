import React, { FC } from 'react';
import { format } from 'date-fns';

import { TableCellContainer } from './TableCell.styled';
import { AdditionalDetails } from '../../../types';

type TabelCellValue = string | number | AdditionalDetails[];

type TableCellProps = {
  value: TabelCellValue;
  type?: string;
  width?: number;
};

export const extractGenreFirstName = (genres: AdditionalDetails[]): string => {
  if (genres) {
    return genres.map((genre) => genre.name)[0];
  }

  return '-';
};

const isAdditionalDetailType = (value: TabelCellValue) => {
  return typeof value !== 'string' && typeof value !== 'number';
};

const FORMATTERS = {
  date: (value: TabelCellValue) => typeof value === 'string' && format(value, 'yyyy/MM/dd'),
  genre: (value: TabelCellValue) => isAdditionalDetailType(value) && extractGenreFirstName(value),
  rating: (value: TabelCellValue) => typeof value === 'string' && value.split(' ')[0],
};

export const TableCell: FC<TableCellProps> = ({ value, type, width }) => {
  const renderedValue = type && FORMATTERS[type] ? FORMATTERS[type](value) : value;
  return <TableCellContainer $width={width}>{renderedValue || '-'}</TableCellContainer>;
};

export default TableCell;
