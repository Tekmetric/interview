import React from 'react';
import { DataGrid } from '@mui/x-data-grid';
import Paper from '@mui/material/Paper';
import { ITableProps } from './Table.interface';

const pageSizeOptions = [5, 10, 15];

const Table:  React.FC<ITableProps> = (props) => {
  return (
    <Paper sx={{ height: 400, width: '100%' }}>
      <DataGrid
        {...props}
        pageSizeOptions={pageSizeOptions}
        sx={{ border: 0 }}
      />
    </Paper>
  );
}

export default Table;
