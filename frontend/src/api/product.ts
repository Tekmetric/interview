import { request } from './queryClient.ts';

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
