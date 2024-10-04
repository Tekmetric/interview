import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { BqPageTitle } from '@beeq/react';

import type { Category, Product } from '../api/service.types';
import { fetchCategoryProducts, fetchProducts } from '../api/service';
import { capitalize } from '../utils';

import { ProductCard } from '../components';

export const Products: React.FC = () => {
  const params = useParams<{ category: Category }>();
  const category = params.category;

  const [products, setProducts] = useState<Product[]>([]);

  useEffect(() => {
    const fetch = async () => {
      try {
        const response = category ? await fetchCategoryProducts(category) : await fetchProducts();
        // Current API returns 200 status code even if there is no data.
        if (!response.data) throw new Error('No products data found');

        setProducts(response.data);
      } catch (error) {
        // Note: This is a simple error handling, in a real project scenario we should handle it properly.
        console.error(error);
      }
    };

    fetch();
  }, [category]);

  return (
    <>
      <BqPageTitle className="[&::part(base)]:p-bs-0">{category ? capitalize(category) : 'All'} products</BqPageTitle>
      <div className="grid grid-cols-4 grid-rows-2 m-bs-m md:m-bs-l xl:m-bs-xl gap-m md:gap-l xl:gap-xl">
        {products.map((product) => (
          <ProductCard
            key={product.id}
            id={product.id}
            title={product.title}
            description={product.description}
            price={product.price}
            image={product.image}
          />
        ))}
      </div>
    </>
  );
};
