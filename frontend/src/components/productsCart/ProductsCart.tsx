import React, { useContext } from 'react';
import { ProductsCartContext } from '../../contexts/ProductsCartContext';
import ProductsListWrapper from './ProductsListWrapper';
import InformationCart from './InformationCart';
import { Typography } from '@mui/material';
import NoData from './NoData';

const ProductsCart = () => {
  const { products } = useContext(ProductsCartContext);

  return (
    <div className="bg-gray-50 border-gray-500 border-r-2 p-3">
      <Typography variant="h4" component="h4">
        Shopping Cart
      </Typography>
      <NoData />
      <ProductsListWrapper
        products={products}
        className="md:grid-cols-1 lg:grid-cols-1"
      />
      <InformationCart />
    </div>
  );
};

export default ProductsCart;
