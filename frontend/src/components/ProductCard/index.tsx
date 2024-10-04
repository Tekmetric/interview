import React from 'react';
import { BqCard } from '@beeq/react';

import { Product } from '../../api/service.types';

import './style.css';

export const ProductCard: React.FC<Product> = ({ id, title, description, price, image }) => {
  return (
    <BqCard
      id={String(id)}
      className="product-card group col-span-2 lg:col-span-1 cursor-pointer transition-transform duration-300 hover:-translate-y-1"
      type="minimal"
    >
      <div className="aspect-[5/7] square w-full overflow-hidden">
        <img
          className="h-full w-full p-m object-contain object-center group-hover:scale-105 transition-transform duration-300"
          src={image}
        />
      </div>
      <div className="flex flex-col grow p-m bg-bg-secondary">
        <h2 className="text-l font-semibold line-clamp-1 text-pretty">{title}</h2>
        <p className="p-bs-xs text-text-secondary text-s leading-small line-clamp-2">{description}</p>
        <span className="m-bs-l text-text-primary text-xl font-semibold">{`$${price}`}</span>
      </div>
    </BqCard>
  );
};
