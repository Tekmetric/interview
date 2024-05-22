import React, { createContext, useState } from 'react';
import useGetProducts from "../hooks/useGetProducts";
import useGetCategories from "../hooks/useGetCategories";
import {SORTING} from "../constants";

const ProductsContext = createContext(undefined);

const ProductsProvider = ({ children }) => {
  const [selectedCategory, setSelectedCategory] = useState(undefined);
  const [selectedSort, setSelectedSort] = useState(SORTING.ASC)
  const [limit, setLimit] = useState(20)
  const { products, loading, error } = useGetProducts(selectedCategory, selectedSort, limit);
  const { categories } = useGetCategories()

  const contextValue = {
    products,
    loading,
    error,
    categories,
    selectedCategory,
    setSelectedCategory,
    selectedSort,
    setSelectedSort,
    limit,
    setLimit
  };

  return (
    <ProductsContext.Provider value={contextValue}>{children}</ProductsContext.Provider>
  );
};

export { ProductsContext, ProductsProvider };
