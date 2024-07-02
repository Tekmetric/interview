import withClearAllWrapper from './clearAllWrapper';
import ProductsList from '../products/ProductsList';
import React from 'react';

const ProductsListWrapper = withClearAllWrapper(ProductsList);
export default ProductsListWrapper;
