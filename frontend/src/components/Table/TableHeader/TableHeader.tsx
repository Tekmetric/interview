import React, { FC } from 'react';

import { TableHeaderContainer } from './TableHeader.styled';

type TableHeaderCell = {
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
  console.log('TableHeader component rendered');

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
