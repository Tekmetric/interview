import React from 'react';
import { Product } from '../../model/Product';
import ProductItem from './ProductItem';

type Props = {
  products: Product[];
  className?: string;
  children?: any;
};

const ProductsList = ({
  products,
  className = 'md:grid-cols-2 lg:grid-cols-4',
  children,
}: Props) => (
  <>
    <div className={`grid gap-4 m-10 sm:grid-cols-1 ${className}`}>
      {products.map((product: Product) => (
        <ProductItem product={product} key={product.id} />
      ))}
    </div>
    {children}
  </>
);

export default React.memo(ProductsList);
