import { Cart } from '../types/Cart.ts';
import {
  createContext,
  ReactNode,
  useCallback,
  useEffect,
  useMemo,
  useState,
} from 'react';
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

const emptyCart: Cart = {
  id: 0,
  products: [],
  total: 0,
  discountedTotal: 0,
  totalProducts: 0,
  totalQuantity: 0,
};

export const CartProvider = ({ children }: CartProviderProps) => {
  const { cart: initialCartData, error } = useGetCartInitialData();
  const [cart, setCart] = useState<Cart>(emptyCart);

  useEffect(() => {
    if (!error && initialCartData) {
      setCart(initialCartData);
    }
  }, [error, initialCartData]);

  const updateCartContext = useCallback((cart: Cart) => {
    setCart(cart);
  }, []);

  const contextValue = useMemo(
    () => ({ cart, updateCartContext }),
    [cart, updateCartContext]
  );

  return (
    <CartContext.Provider value={contextValue}>{children}</CartContext.Provider>
  );
};
