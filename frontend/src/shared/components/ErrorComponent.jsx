import React from 'react';
import { PropTypes } from 'prop-types';
import { Box, Typography } from '@mui/material';

export default function ErrorComponent({ error }) {
  return (
    <Box display="flex" alignItems="center">
      <Typography color="error">{error.message}</Typography>
    </Box>
  );
}

ErrorComponent.propTypes = {
  error: PropTypes.object
};
