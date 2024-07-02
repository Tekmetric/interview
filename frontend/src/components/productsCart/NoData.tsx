import { useContext } from 'react';
import { ProductsCartContext } from '../../contexts/ProductsCartContext';
import { Typography } from '@mui/material';

const NoData = () => {
  const { products } = useContext(ProductsCartContext);

  return (
    !products.length && (
      <Typography variant="h6" component="h6">
        No Products added yet.
      </Typography>
    )
  );
};

export default NoData;
