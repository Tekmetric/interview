import React from 'react';

interface TableCellProps {
  width?: string;
  className?: string;
  children?: React.ReactNode;
}

export const TableCell: React.FC<TableCellProps> = ({ width, className, children }) => (
  <div style={{width}} className={className}>
    {children}
  </div>
);

export const TableHeaderCell = TableCell;
