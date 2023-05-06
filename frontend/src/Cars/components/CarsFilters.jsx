import React from 'react';
import { FormControl, InputLabel, Select, MenuItem, Grid } from '@mui/material';
import { useSearchParams } from 'react-router-dom';
import { useQuery } from 'react-query';
import { getBrands, getColors } from '../api';

export default function CarsFilters() {
  const { data: brands, isLoading: isBrandsLoading } = useQuery('/cars/brands', getBrands);
  const { data: colors, isLoading: isColorsLoading } = useQuery('/cars/colors', getColors);
  const [searchParams, setSearchParams] = useSearchParams({ brand: 'all', color: 'all' });

  const handleFilterChange = (key, value) => {
    searchParams.set(key, value);
    setSearchParams(searchParams);
  };

  return (
    <Grid container spacing={2}>
      <Grid item xs={12} md={6} lg={4}>
        {!isBrandsLoading && (
          <FormControl fullWidth>
            <InputLabel id="brand-select-label">Brand</InputLabel>
            <Select
              labelId="brand-select-label"
              value={searchParams.get('brand')}
              label="Brand"
              onChange={(e) => handleFilterChange('brand', e.target.value)}>
              {brands.map((brand) => (
                <MenuItem key={brand.key} value={brand.key}>
                  {brand.value}
                </MenuItem>
              ))}
            </Select>
          </FormControl>
        )}
      </Grid>
      <Grid item xs={12} md={6} lg={4}>
        {!isColorsLoading && (
          <FormControl fullWidth>
            <InputLabel id="color-select-label">Color</InputLabel>
            <Select
              labelId="color-select-label"
              value={searchParams.get('color')}
              label="Color"
              onChange={(e) => handleFilterChange('color', e.target.value)}>
              {colors.map((color) => (
                <MenuItem key={color.key} value={color.key}>
                  {color.value}
                </MenuItem>
              ))}
            </Select>
          </FormControl>
        )}
      </Grid>
    </Grid>
  );
}
