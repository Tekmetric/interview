import { putRequest, request } from './queryClient.ts';
import { Cart, UpdateCartItemsPayload } from '../types/Cart.ts';

export const getCart = async (id: string): Promise<Cart> => {
  return await request({
    url: `https://dummyjson.com/carts/${id}`,
  });
};

export const updateCart = async (
  id: string,
  data: UpdateCartItemsPayload
): Promise<Cart> => {
  return await putRequest({
    url: `https://dummyjson.com/carts/${id}`,
    body: data,
  });
};
