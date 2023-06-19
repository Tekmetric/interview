import React from 'react';
import { Grid } from '@chakra-ui/react';
import useProductsContext from "../../context/useProductsContext";
import Loader from "../loader/Loader";
import ErrorMessage from "../error/ErrorMessage";
import ProductItem from "../productItem/ProductItem";


const Products = () => {
  const { products, loading, error } = useProductsContext();

  if (loading) {
    return <Loader />;
  }

  if (error) {
    return <ErrorMessage message={error.message} />;
  }

  return (
    <Grid templateColumns="repeat(auto-fit, minmax(240px, 1fr))" gap={4}>
      {products.map((product) => (
        <ProductItem key={product.id} product={product} />
      ))}
    </Grid>
  );
};

export default Products;
