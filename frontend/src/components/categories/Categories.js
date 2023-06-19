import React, {useCallback} from 'react';
import {Box, Select} from '@chakra-ui/react';
import useProductsContext from "../../context/useProductsContext";

const Categories = () => {
  const { categories, selectedCategory, setSelectedCategory } = useProductsContext()

  const handleCategoryChange = useCallback((event) => {
    const category = event.target.value;
    setSelectedCategory(category);
  }, [setSelectedCategory]);

  if (!categories) {
    return null
  }

  return (
    <Box>
      <Select placeholder="Select category" value={selectedCategory} onChange={handleCategoryChange}>
        {categories.map((category) => (
          <option key={category} value={category}>
            {category}
          </option>
        ))}
      </Select>
    </Box>
  );
};

export default Categories;
