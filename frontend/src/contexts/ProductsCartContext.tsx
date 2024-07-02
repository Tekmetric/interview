import React, { createContext } from 'react';
import { Product } from '../model/Product';

interface ProductsContextType {
  products: Product[];
  handleProduct: (product: Product) => void;
  clear: () => void;
}

const defaultContextValue: ProductsContextType = {
  products: [],
  handleProduct: () => {},
  clear: () => {},
};

export const ProductsCartContext =
  createContext<ProductsContextType>(defaultContextValue);
