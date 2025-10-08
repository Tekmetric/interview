import React, { FC } from 'react';

import { TableHeaderContainer } from './TableHeader.styled';

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
        <div key={header.key} style={{ width: `${header.size}%` }}>
          {header.label}
        </div>
      ))}
    </TableHeaderContainer>
  );
};

export default TableHeader;
