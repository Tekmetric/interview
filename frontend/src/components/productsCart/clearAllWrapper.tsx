import React, { useContext } from 'react';
import { ProductsCartContext } from '../../contexts/ProductsCartContext';
import { Button } from '@mui/material';

const withClearAllWrapper = <P extends object>(
  WrappedComponent: React.ComponentType<P>,
) => {
  return (props: any) => {
    const { clear, products } = useContext(ProductsCartContext);

    const getChildren = () =>
      products.length > 0 && <Button onClick={clear}> Clear</Button>;

    return <WrappedComponent {...props}>{getChildren()}</WrappedComponent>;
  };
};

export default withClearAllWrapper;
