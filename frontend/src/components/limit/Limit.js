import React, {useCallback} from 'react';
import { Box, Text, Select } from '@chakra-ui/react';
import useProductsContext from "../../context/useProductsContext";

const Limit = () => {
  const { limit, setLimit } = useProductsContext();

  const handleLimitChange = useCallback((event) => {
    const newLimit = parseInt(event.target.value);
    setLimit(newLimit);
  }, [setLimit]);

  return (
    <Box display="flex" alignItems="center">
      <Text fontWeight="bold" mr={2}>
        Show:
      </Text>
      <Select value={limit} onChange={handleLimitChange}>
        <option value={5}>5</option>
        <option value={10}>10</option>
        <option value={20}>20</option>
      </Select>
    </Box>
  );
};

export default Limit;
