import React, { useEffect, useState } from 'react';
import { ProductCard } from '../components';
import { Product } from '../api/service.types';
import { fetchProducts } from '../api/service';

export const AllProducts: React.FC = () => {
  const [products, setProducts] = useState<Product[]>([]);

  useEffect(() => {
    const fetch = async () => {
      try {
        const response = await fetchProducts();
        // Current API returns 200 status code even if there is no data.
        if (!response.data) throw new Error('No products data found');

        setProducts(response.data);
      } catch (error) {
        // Note: This is a simple error handling, in a real project scenario we should handle it properly.
        console.error(error);
      }
    };

    fetch();
  }, []);

  return (
    <div className="grid grid-cols-4 grid-rows-2 gap-m md:gap-l xl:gap-xl">
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
  );
};
