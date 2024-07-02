import React, { useState, useEffect } from 'react';
import { getProducts } from '../services/service';
import { Product } from '../model/Product';
import { ProductsCartContext } from '../contexts/ProductsCartContext';
import ProductsList from '../components/products/ProductsList';
import Notification from '../components/notification/Notification';
import ProductsCart from '../components/productsCart/ProductsCart';
import { CircularProgress } from '@mui/material';

const Home = (): React.ReactElement => {
  const [loading, setLoading] = useState(true);
  const [products, setProducts] = useState<Product[]>([]);
  const [favoriteProducts, setFavoriteProducts] = useState<Product[]>([]);

  useEffect(() => {
    getData();
  }, []);

  const getData = async () => {
    setLoading(true);
    try {
      const data: Product[] = await getProducts();
      setProducts(data);
      setLoading(false);
    } catch {
      setLoading(false);
      setProducts([]);
    }
  };
  const handleProductClick = (product: Product) => {
    const productAlreadyThere = favoriteProducts.find(
      (item) => item.id === product.id,
    );
    if (!productAlreadyThere) {
      setFavoriteProducts([...favoriteProducts, product]);
    } else {
      setFavoriteProducts(
        favoriteProducts.filter((item) => item.id !== product.id),
      );
    }
  };

  const clear = () => setFavoriteProducts([]);

  if (loading) {
    return <CircularProgress color="primary" />;
  }

  return (
    <div className="grid grid-cols-[max-content_1fr]">
      <ProductsCartContext.Provider
        value={{
          products: favoriteProducts,
          handleProduct: handleProductClick,
          clear,
        }}
      >
        <ProductsList products={products} />
        <ProductsCart />
        <Notification />
      </ProductsCartContext.Provider>
    </div>
  );
};

export default Home;
