import { Button, Grid, TextField } from '@mui/material';
import { DatePicker } from '@mui/x-date-pickers';
import dayjs from 'dayjs';
import PropTypes from 'prop-types';

export default function CarForm({ formik }) {
  return (
    <form onSubmit={formik.handleSubmit}>
      <Grid container sx={{ py: 5, px: 10 }} spacing={2}>
        <Grid item xs={12} md={6}>
          <TextField
            fullWidth
            id="brand"
            name="brand"
            label="Brand"
            value={formik.values.brand}
            onChange={formik.handleChange}
            error={formik.touched.brand && Boolean(formik.errors.brand)}
            helperText={formik.touched.brand && formik.errors.brand}
            sx={{ mb: 2 }}
          />
          <TextField
            fullWidth
            id="model"
            name="model"
            label="Model"
            value={formik.values.model}
            onChange={formik.handleChange}
            error={formik.touched.model && Boolean(formik.errors.model)}
            helperText={formik.touched.model && formik.errors.model}
            sx={{ mb: 2 }}
          />
          <TextField
            fullWidth
            id="url"
            name="url"
            label="Image URL"
            value={formik.values.url}
            onChange={formik.handleChange}
            error={formik.touched.url && Boolean(formik.errors.url)}
            helperText={formik.touched.url && formik.errors.url}
            sx={{ mb: 2 }}
          />
          <TextField
            fullWidth
            id="minPrice"
            name="minPrice"
            label="Minimum Price"
            type="number"
            value={formik.values.minPrice}
            onChange={formik.handleChange}
            error={formik.touched.minPrice && Boolean(formik.errors.minPrice)}
            helperText={formik.touched.minPrice && formik.errors.minPrice}
            sx={{ mb: 2 }}
          />
        </Grid>
        <Grid item xs={12} md={6}>
          <DatePicker
            id="year"
            name="year"
            label="Manufacturing Year"
            value={dayjs().year(formik.values.year)}
            onChange={(e) => {
              formik.handleChange(e.year().toString());
            }}
            error={formik.touched.year && Boolean(formik.errors.year)}
            helperText={formik.touched.year && formik.errors.year}
            views={['year']}
            sx={{ mb: 2, width: '100%' }}
          />
          <TextField
            fullWidth
            id="engineCapacity"
            name="engineCapacity"
            label="Engine Capacity (cmc)"
            type="number"
            value={formik.values.engineCapacity}
            onChange={formik.handleChange}
            error={formik.touched.engineCapacity && Boolean(formik.errors.engineCapacity)}
            helperText={formik.touched.engineCapacity && formik.errors.engineCapacity}
            sx={{ mb: 2 }}
          />
          <TextField
            fullWidth
            id="color"
            name="color"
            label="Color"
            value={formik.values.color}
            onChange={formik.handleChange}
            error={formik.touched.color && Boolean(formik.errors.color)}
            helperText={formik.touched.color && formik.errors.color}
            sx={{ mb: 2 }}
          />
          <TextField
            fullWidth
            id="maxPrice"
            name="maxPrice"
            label="Maximum Price"
            type="number"
            value={formik.values.maxPrice}
            onChange={formik.handleChange}
            error={formik.touched.maxPrice && Boolean(formik.errors.maxPrice)}
            helperText={formik.touched.maxPrice && formik.errors.maxPrice}
            sx={{ mb: 2 }}
          />
        </Grid>
        <Grid item xs={12}>
          <TextField
            fullWidth
            multiline
            id="description"
            name="description"
            label="Description"
            value={formik.values.description}
            onChange={formik.handleChange}
            error={formik.touched.description && Boolean(formik.errors.description)}
            helperText={formik.touched.description && formik.errors.description}
            sx={{ mb: 2 }}
          />
        </Grid>
        <Grid item xs={12}>
          <Button color="primary" variant="contained" fullWidth type="submit">
            Submit
          </Button>
        </Grid>
      </Grid>
    </form>
  );
}

CarForm.propTypes = {
  formik: PropTypes.object.isRequired
};
