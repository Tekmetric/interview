import React, { useContext } from 'react';
import { ProductsCartContext } from '../../contexts/ProductsCartContext';
import { Product } from '../../model/Product';

type Props = {
  children: React.ReactElement | null | string;
  product: Product;
};

const ActionButton = ({ children, product }: Props) => {
  const { handleProduct, products } = useContext(ProductsCartContext);

  const onClickHandler = () => {
    handleProduct(product);
  };

  const getMessage = () => {
    return Boolean(products.find((item: Product) => item.id === product.id))
      ? 'Remove from '
      : 'Add to ';
  };

  return (
    <button
      onClick={onClickHandler}
      className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded"
    >
      {getMessage()}
      {children}
    </button>
  );
};

export default ActionButton;
