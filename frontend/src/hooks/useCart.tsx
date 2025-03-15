import { useContext } from 'react';
import { CartContext, CartContextType } from '../providers/CartProvider.tsx';
import { UpdateCartItemsPayload } from '../types/Cart.ts';
import { getCart, updateCart } from '../api/cart.ts';
import { useMutation, useQuery } from '@tanstack/react-query';

const CART_ID = '10'; // mocked an existing cart id

export const useCart = (): CartContextType => {
  const context = useContext(CartContext);

  if (context === undefined) {
    throw new Error('useCart must be used within a CartProvider');
  }

  return context;
};

export const useGetCartInitialData = () => {
  const {
    data: cart,
    isLoading,
    error,
  } = useQuery({
    queryKey: ['cart', CART_ID],
    queryFn: () => getCart(CART_ID),
    staleTime: 1000 * 60 * 5, // 5 minutes
  });

  return { cart, isLoading, error };
};

export const useUpdateCart = () => {
  const { updateCartContext } = useCart();
  const { mutate, isError, isSuccess, isPending } = useMutation({
    mutationFn: (data: UpdateCartItemsPayload) => updateCart(CART_ID, data),
    onSuccess: (cart) => {
      updateCartContext(cart);
    },
  });

  return { mutate, isError, isSuccess, isPending };
};
