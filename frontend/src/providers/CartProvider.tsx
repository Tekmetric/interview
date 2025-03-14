import { Cart } from '../types/Cart.ts';
import { createContext, ReactNode, useEffect, useMemo, useState } from 'react';
import { useGetCartInitialData } from '../hooks/useCart.tsx';

export interface CartContextType {
  cart: Cart;
  updateCartContext: (cart: Cart) => void;
}

export const CartContext = createContext<CartContextType | undefined>(
  undefined
);

interface CartProviderProps {
  children: ReactNode;
}

export const CartProvider = ({ children }: CartProviderProps) => {
  const { cart: initialCartData, error } = useGetCartInitialData();
  const [cart, setCart] = useState<Cart | undefined>(undefined);

  useEffect(() => {
    if (!error && initialCartData) {
      setCart(initialCartData);
    }
  }, [error, initialCartData]);

  const updateCartContext = (cart: Cart) => {
    setCart(cart);
  };

  const contextValue = useMemo(
    () => ({ cart: cart as Cart, updateCartContext }),
    [cart]
  );

  return (
    <CartContext.Provider value={contextValue}>{children}</CartContext.Provider>
  );
};
