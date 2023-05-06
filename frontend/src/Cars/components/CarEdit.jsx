import React from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { Box, Paper, Typography } from '@mui/material';
import { useFormik } from 'formik';
import carValidationSchema from '../validation';
import CarForm from './CarForm';
import { getCar, patchCar } from '../api';
import { useMutation, useQuery } from 'react-query';

function CarEdit() {
  const { id: carId } = useParams();
  const { data: car, isLoading, error } = useQuery('/cars/' + carId, () => getCar(carId));
  const mutation = useMutation('/cars', patchCar);
  const navigate = useNavigate();

  if (isLoading) {
    return 'Loading...';
  }

  if (error) {
    return 'Error ' + error.message;
  }

  const formik = useFormik({
    initialValues: { ...car },
    validationSchema: carValidationSchema,
    onSubmit: (values) => {
      mutation.mutate(carId, values);
      navigate('/');
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
