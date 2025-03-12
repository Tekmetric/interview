import { request } from './queryClient.ts';

export const getProducts = async ({ pageParam }: { pageParam: number }) => {
  return await request({
    url: `https://dummyjson.com/products/search`,
    params: {
      limit: 12,
      skip: pageParam,
      q: '',
    },
  });
};
