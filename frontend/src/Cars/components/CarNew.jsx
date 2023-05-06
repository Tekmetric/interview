import React from 'react';
import { Box, Paper, Typography } from '@mui/material';
import { useFormik } from 'formik';
import carValidationSchema from '../validation';
import CarForm from './CarForm';
import { INITIAL_CAR_VALUES } from '../../shared/constants';
import { useMutation } from 'react-query';
import { postCar } from '../api';
import { useNavigate } from 'react-router-dom';

function CarNew() {
  const mutation = useMutation('/cars', postCar);
  const navigate = useNavigate();

  const formik = useFormik({
    initialValues: { ...INITIAL_CAR_VALUES },
    validationSchema: carValidationSchema,
    onSubmit: (values) => {
      mutation.mutate(values);
      navigate('/');
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
