import React, { useCallback } from 'react';
import { Text, Flex, Icon } from '@chakra-ui/react';
import { ArrowDownIcon, ArrowUpIcon } from '@chakra-ui/icons';
import useProductsContext from "../../context/useProductsContext";
import { SORTING } from "../../constants";

const Sorting = () => {
  const { selectedSort, setSelectedSort } = useProductsContext();
  const { DESC, ASC } = SORTING;

  const handleSortToggle = useCallback(() => {
    setSelectedSort((prevSort) => (prevSort === DESC ? ASC : DESC));
  }, [setSelectedSort, ASC, DESC]);

  return (
    <Flex align="center" cursor="pointer" onClick={handleSortToggle}>
      <Text fontWeight="bold" mr={2}>
        Sorting:
      </Text>
      <Icon as={selectedSort === DESC ? ArrowDownIcon : ArrowUpIcon} boxSize={5} />
    </Flex>
  );
};

export default Sorting;

