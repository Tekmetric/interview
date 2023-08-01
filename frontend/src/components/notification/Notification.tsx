import React, { useState, useEffect, useContext } from 'react';
import { Alert, Snackbar } from '@mui/material';
import { ProductsCartContext } from '../../contexts/ProductsCartContext';
import { usePrevious } from '../../hooks/usePrevious';
import { Product } from '../../model/Product';
import { getNotificationMessage } from '../../helpers/utils';

const Notification = () => {
  const [message, setMessage] = useState<string>('');
  const { products } = useContext(ProductsCartContext);

  const oldProducts: Product[] | undefined = usePrevious(products);

  useEffect(() => {
    if (products.length || oldProducts?.length) {
      const message = getNotificationMessage(
        oldProducts?.length || 0,
        products.length,
      );
      setMessage(message);
    }
  }, [products.length]);

  useEffect(() => {
    const timeout = setTimeout(() => {
      setMessage('');
    }, 1000);

    return () => clearTimeout(timeout);
  }, [message.length]);

  return (
    <Snackbar
      anchorOrigin={{ vertical: 'top', horizontal: 'right' }}
      open={Boolean(message.length)}
    >
      <Alert severity="success" sx={{ width: '100%' }}>
        {message}
      </Alert>
    </Snackbar>
  );
};

export default Notification;
