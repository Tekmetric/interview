import React from 'react';

import { FilterComponent } from '../interfaces/components';
import { Grid, TextField } from '@mui/material';

const Filter: React.FC<FilterComponent> = ({ filters, setFilters }) => {
  return (
    <Grid container className="flex flex-1 align-middle justify-center" spacing={2}>
      <Grid item>
        <TextField
          id="name-filter"
          label="Name"
          variant="standard"
          defaultValue={filters.name}
          onChange={(e) => setFilters({ ...filters, name: e.target.value })}
        />
      </Grid>
      <Grid item>
        <TextField
          id="country-filter"
          label="Country"
          variant="standard"
          defaultValue={filters.country}
          onChange={(e) => setFilters({ ...filters, country: e.target.value })}
        />
      </Grid>
    </Grid>
  );
};

export default Filter;
