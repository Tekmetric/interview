import React from 'react';
import useProductsCart from '../../hooks/useProductsCart';
import CheckoutButton from '../buttons/CheckoutButton';

const InformationCart = () => {
  const { getTotal } = useProductsCart();
  const total = getTotal();

  return (
    total.length > 0 && (
      <div>
        <div className="flex font-semibold justify-between py-6 text-sm uppercase m-1">
          <span>Total: </span>
          <span>{total}</span>
        </div>
        <CheckoutButton />
      </div>
    )
  );
};

export default React.memo(InformationCart);
