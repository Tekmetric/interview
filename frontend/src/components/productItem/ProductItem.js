import {
  Flex,
  Box,
  Image,
  Center,
  Text
} from '@chakra-ui/react';
import Rating from "../rating/Rating";

const ProductItem = ({product}) => {
  const {title, image, price, rating} = product;

  return (
    <Box
      maxW="xs"
      shadow="lg"
      position="relative">
      <Flex mt="1" justifyContent="space-between" direction="column" h="100%">
        <Flex alignContent="center" w="100%" justifyContent="center" flexGrow="1">
          <Box p="6" h="full" >
            <Center minH={200}>
              <Image src={image} alt={`Picture of ${title}`} roundedTop="lg" maxH={200}/>
            </Center>
          </Box>
        </Flex>
        <Box p="6">
          <Text
            fontSize="2xl"
            fontWeight="semibold"
            as="h4"
            isTruncated>
            {title}
          </Text>

          <Flex justifyContent="space-between" alignContent="center">
            <Rating rating={rating}/>
            <Text fontSize="xl">
              <Box as="span" color="gray.600" fontSize="lg">
                $
              </Box>
              {price.toFixed(2)}
            </Text>
          </Flex>
        </Box>
      </Flex>
    </Box>
  );
}

export default ProductItem;
