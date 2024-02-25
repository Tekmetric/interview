import React from 'react';
import { Grid, TextField } from '@mui/material';

import { FilterComponentI, FiltersI } from '../interfaces/components';
import useDebounce from '../customHooks/debounce';

const Filter: React.FC<FilterComponentI> = ({ filters, setFilters }) => {
  
  const handleSearch = useDebounce((filter: FiltersI) => {
    setFilters(filter);
  }, 500);

  const handleChange = (key: string, val: string) => {
    let newFilter = { ...filters, [key]: val };
    handleSearch(newFilter);
  };

  return (
    <Grid
      container
      className="flex flex-1 align-middle justify-center"
      spacing={2}
    >
      <Grid item>
        <TextField
          id="name-filter"
          label="Name"
          variant="standard"
          defaultValue={filters.name}
          onChange={(e) => handleChange('name', e.target.value)}
        />
      </Grid>
      <Grid item>
        <TextField
          id="country-filter"
          label="Country"
          variant="standard"
          defaultValue={filters.country}
          onChange={(e) => handleChange('country', e.target.value)}
        />
      </Grid>
    </Grid>
  );
};

export default Filter;
