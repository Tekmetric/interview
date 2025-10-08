import React, { FC, useRef } from 'react';

import { TableBodyContainer } from './TableBody.styled';
import { TableBodyRowProps, TableBodyRow } from '../TableRow/TableRow';

export type TableBodyProps = {
  rows: TableBodyRowProps[];
  fetchNextPage: () => {};
};

export const TableBody: FC<TableBodyProps> = ({ fetchNextPage, rows }) => {
  console.log('TableBody', fetchNextPage);
  const scrollRef = useRef<HTMLDivElement>(null);

  return (
    <TableBodyContainer ref={scrollRef}>
      {rows.map((row) => (
        <TableBodyRow row={row} key={row.id} />
      ))}
    </TableBodyContainer>
  );
};

export default TableBody;
