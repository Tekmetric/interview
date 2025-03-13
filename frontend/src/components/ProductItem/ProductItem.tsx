import React from 'react';

import { Product } from '../../types/Product.ts';
import Rating from '../Rating/Rating.tsx';
import Image from '../Image/Image.tsx';
import { Link } from 'react-router';

interface ProductItemProps {
  product: Product;
}

const ProductItem = ({ product }: ProductItemProps) => {
  return (
    <Link
      to={`/product/${product.id}`}
      className="flex w-full cursor-pointer flex-col gap-2 focus:shadow-sm focus:outline-none"
    >
      <div className="relative flex items-center justify-center border border-gray-300 bg-white">
        <Image
          src={product.thumbnail}
          alt={product.title}
          className="h-50 w-full object-contain"
        />
      </div>
      <div className="flex flex-col gap-1">
        <span className="text-base text-neutral-800">{product.title}</span>
        <Rating
          rating={product.rating}
          reviewsCount={product.reviews?.length}
        />
        <span className="text-sm text-neutral-500">${product.price}</span>
      </div>
    </Link>
  );
};

export default React.memo(ProductItem);
