import { useEffect, useRef, useState } from 'react';
import { Link } from 'react-router';

import { useCart } from '../../hooks/useCart.tsx';

import { ShoppingCart as ShoppingCartIcon } from 'lucide-react';
import { twMerge } from 'tailwind-merge';

const ShoppingCart = () => {
  const { cart } = useCart();
  const prevQuantityRef = useRef(cart?.totalQuantity ?? 0);
  const [isAnimating, setIsAnimating] = useState(true);

  useEffect(() => {
    const timer = setTimeout(() => {
      setIsAnimating(false);
    }, 1000); // Animation plays for 1 second

    return () => clearTimeout(timer);
  }, []);

  // Trigger animation if cart quantity changes
  useEffect(() => {
    if (cart && prevQuantityRef.current !== cart.totalQuantity) {
      setIsAnimating(true);

      const timer = setTimeout(() => {
        setIsAnimating(false);
      }, 1000);

      prevQuantityRef.current = cart.totalQuantity;

      return () => clearTimeout(timer);
    }
  }, [cart?.totalQuantity]);

  return (
    <Link to="/cart" className="relative ml-auto flex">
      <ShoppingCartIcon className="h-8 w-8" />
      <span
        className={twMerge(
          'absolute left-5 flex h-[18px] w-[18px] items-center justify-center rounded-full bg-red-500 pt-[1px] text-center text-[10px] leading-[18px] text-white',
          isAnimating ? 'animate-bounce' : ''
        )}
      >
        {cart?.totalQuantity ?? 0}
      </span>
    </Link>
  );
};

export default ShoppingCart;
