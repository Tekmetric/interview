import * as React from 'react';
import { DataGrid } from '@mui/x-data-grid';
import Paper from '@mui/material/Paper';
import { ITableProps } from './Table.interface';

const pageSizeOptions = [5, 10, 15];

export default function Table(props: ITableProps) {
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
