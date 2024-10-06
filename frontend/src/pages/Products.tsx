import React from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useQuery } from 'react-query';
import { BqButton, BqIcon, BqPageTitle } from '@beeq/react';

import type { Category } from '../api/service.types';
import { fetchCategoryProducts, fetchProducts } from '../api/service';
import { capitalize } from '../utils';

import { NoResults, ProductCard, Spinner } from '../components';

const retrieveProducts = async (category?: Category) => {
  try {
    const response = category ? await fetchCategoryProducts(category) : await fetchProducts();
    // Current API returns 200 status code even if there is no data.
    if (!response.data) throw new Error('No products data found');
    return response.data;
  } catch (error) {
    return Promise.reject(error);
  }
};

export const Products: React.FC = () => {
  const navigate = useNavigate();
  const { category } = useParams<{ category: Category }>();
  // Note: We can use the `useQuery` hook to fetch data from the API. It will automatically handle the loading state.
  const { data: products, isError, isLoading } = useQuery(['products', category], () => retrieveProducts(category));

  return (
    <>
      <BqPageTitle className="[&::part(base)]:p-bs-0">
        {category && (
          <BqButton appearance="link" slot="back" onBqClick={() => navigate('/')}>
            <BqIcon
              name="arrow-left-bold"
              color="text--primary"
              slot="prefix"
              role="img"
              title="Navigate back to the home page"
            />
          </BqButton>
        )}
        {category ? capitalize(category) : 'All'} products
      </BqPageTitle>
      {/* Show a spinner while the data is loading */}
      {isLoading && <Spinner />}
      {/* Show a message if there is an error or no products are found */}
      {(isError || products?.length === 0) && <NoResults />}
      {/* Show the products catalogue */}
      {products && products?.length > 0 && (
        <div className="grid grid-cols-1 grid-rows-2 gap-m m-bs-m sm:grid-cols-2 md:grid-cols-4 md:gap-l md:m-bs-l xl:gap-xl xl:m-bs-xl">
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
      )}
    </>
  );
};
