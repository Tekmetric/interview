import React from 'react';

export const TableCell = ({ width, className, children }) => (
  <div style={{width}} className={className}>
    {children}
  </div>
);

export const TableHeaderCell = TableCell;
