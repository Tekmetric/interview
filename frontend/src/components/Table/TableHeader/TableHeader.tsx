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
      <tr>
        {headers.map((header) => (
          <th key={header.key} style={{ width: `${header.size}%` }}>
            {header.label}
          </th>
        ))}
      </tr>
    </TableHeaderContainer>
  );
};

export default TableHeader;
