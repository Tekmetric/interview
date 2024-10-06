import React from 'react';
import { BqCard } from '@beeq/react';

import { Product } from '../../api/service.types';

import './style.css';

export const ProductCard: React.FC<Product> = ({ id, title, description, price, image }) => {
  return (
    <BqCard
      id={String(id)}
      className="product-card group cursor-pointer transition-transform duration-300 hover:-translate-y-1"
      type="minimal"
    >
      <div className="square aspect-[5/7] w-full overflow-hidden">
        <img
          alt={title}
          className="h-full w-full object-contain object-center p-m transition-transform duration-300 group-hover:scale-105"
          loading="lazy"
          src={image}
        />
      </div>
      <div className="flex grow flex-col bg-bg-secondary p-m">
        <h2 className="line-clamp-1 text-pretty text-l font-semibold">{title}</h2>
        <p className="line-clamp-2 text-s leading-small text-text-secondary p-bs-xs">{description}</p>
        <span className="text-xl font-semibold text-text-primary m-bs-l">{`$${price}`}</span>
      </div>
    </BqCard>
  );
};
