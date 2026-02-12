import React, { FC, useRef } from 'react';

import { TableBodyScroll, TableBodyContainer } from './TableBody.styled';
import { TableBodyRow } from '../TableRow/TableRow';
import { ROW_HEIGHT } from '../TableRow/TableRow.styled';

import { TableBodyProps } from '../Table.types';

export const TableBody: FC<TableBodyProps> = ({ isFetching, fetchNextPage, rows }) => {
  const containerRef = useRef<HTMLDivElement>(null);
  const scrollRef = useRef<HTMLDivElement>(null);

  const handleScroll = () => {
    if (!isFetching) {
      const { scrollTop, clientHeight } = scrollRef.current;

      if (((rows.length || 0) * ROW_HEIGHT - scrollTop - clientHeight) < (ROW_HEIGHT + 20)) {
        fetchNextPage();
      }
    }
  };

  return (
    <TableBodyContainer ref={containerRef}>
      <TableBodyScroll ref={scrollRef} onScroll={() => handleScroll()}>
        {rows.map((row) => (
          <TableBodyRow row={row} key={row.id} />
        ))}
      </TableBodyScroll>
    </TableBodyContainer>
  );
};

export default TableBody;
