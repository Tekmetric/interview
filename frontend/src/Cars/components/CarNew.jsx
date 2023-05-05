import React from 'react';
import { Box, Paper, Typography } from '@mui/material';
import { useFormik } from 'formik';
import carValidationSchema from '../validation';
import CarForm from './CarForm';
import { INITIAL_CAR_VALUES } from '../../shared/constants';

function CarNew() {
  const formik = useFormik({
    initialValues: { ...INITIAL_CAR_VALUES },
    validationSchema: carValidationSchema,
    onSubmit: (values) => {
      alert(JSON.stringify(values, null, 2));
    }
  });

  return (
    <Box display="flex" flexDirection="column" alignItems="center" sx={{ pt: 5, px: 1 }}>
      <Paper sx={{ maxWidth: 'lg' }}>
        <Typography align="center" variant="h3" gutterBottom>
          New Car
        </Typography>
        <CarForm formik={formik} />
      </Paper>
    </Box>
  );
}

export default CarNew;
