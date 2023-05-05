import React from 'react';
import { CARS } from '../../shared/constants';
import { useParams } from 'react-router-dom';
import { Box, Paper, Typography } from '@mui/material';
import { useFormik } from 'formik';
import carValidationSchema from '../validation';
import CarForm from './CarForm';

function getCarById(carId) {
  return CARS.find((car) => car.id == carId);
}

function CarEdit() {
  const { id: carId } = useParams();
  const car = getCarById(carId);

  const formik = useFormik({
    initialValues: { ...car },
    validationSchema: carValidationSchema,
    onSubmit: (values) => {
      alert(JSON.stringify(values, null, 2));
    }
  });

  return (
    <Box display="flex" flexDirection="column" alignItems="center" sx={{ pt: 5, px: 1 }}>
      <Paper sx={{ maxWidth: 'lg' }}>
        <Typography align="center" variant="h3" gutterBottom>
          Edit Car
        </Typography>
        <CarForm formik={formik} />
      </Paper>
    </Box>
  );
}

export default CarEdit;
