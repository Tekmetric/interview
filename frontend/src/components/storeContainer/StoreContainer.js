import {Box, Center, Flex, Spacer} from "@chakra-ui/react";
import Categories from "../categories/Categories";
import Sorting from "../sorting/Sorting";
import Limit from "../limit/Limit";
import Products from "../products/Products";

const StoreContainer = () => {
  return (
    <Center p={6}>
      <Box maxW="1200px" w="100%">
        <Box p={4} boxShadow="md" mb={4}>
          <Flex alignItems="center">
            <Categories/>
            <Spacer/>
            <Limit/>
            <Spacer/>
            <Sorting/>
          </Flex>
        </Box>
        <Products/>
      </Box>
    </Center>
  );
};

export default StoreContainer;
