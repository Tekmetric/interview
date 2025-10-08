import React, { FC } from 'react';

import { TableHeaderContainer } from './TableHeader.styled';
import TableCell from '../TableCell/TableCell';

export type TableHeaderCell = {
  label: string;
  key: string;
  isSortable: boolean;
  sortDirection?: 'asc' | 'desc';
  size: number;
};

type TableHeaderProps = {
  headers: TableHeaderCell[];
};

export const TableHeader: FC<TableHeaderProps> = ({
  headers,
}) => {
  return (
    <TableHeaderContainer>
      {headers.map((header) => (
        <TableCell key={header.key} width={header.size} value={header.label} />
      ))}
    </TableHeaderContainer>
  );
};

export default TableHeader;
