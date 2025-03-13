import { request } from './queryClient.ts';
import { Product } from '../types/Product.ts';

export const getProducts = async ({
  pageParam,
  search = '',
}: {
  pageParam: number;
  search: string;
}) => {
  return await request({
    url: `https://dummyjson.com/products/search`,
    params: {
      limit: 12,
      skip: pageParam,
      q: search,
    },
  });
};

export const getProduct = async (id: string): Promise<Product> => {
  return await request({
    url: `https://dummyjson.com/products/${id}`,
  });
};
