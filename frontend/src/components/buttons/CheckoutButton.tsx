import React, { useContext } from 'react';
import { Button } from '@mui/material';
import { ProductsCartContext } from '../../contexts/ProductsCartContext';

const CheckoutButton = () => {
  const { clear } = useContext(ProductsCartContext);

  return (
    <div className="m-auto">
      <Button onClick={clear} variant="outlined">
        Checkout
      </Button>
    </div>
  );
};

export default React.memo(CheckoutButton);
