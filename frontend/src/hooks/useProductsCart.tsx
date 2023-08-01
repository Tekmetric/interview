import { useContext, useEffect, useState } from 'react';
import { ProductsCartContext } from '../contexts/ProductsCartContext';
import { formatNumberValue } from '../helpers/utils';

const useProductsCart = (): { getTotal: () => string } => {
  const [total, setTotal] = useState<number>(0);
  const { products } = useContext(ProductsCartContext);

  useEffect(() => {
    setTotal(products.reduceRight((acc, product) => acc + product.price, 0));
  }, [products.length]);

  const getTotal = (): string => {
    return (total > 0 && formatNumberValue(total)) || '';
  };

  return {
    getTotal,
  };
};

export default useProductsCart;
