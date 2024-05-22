import { Box, Flex, Text, Alert, AlertIcon, AlertDescription } from '@chakra-ui/react';

const ErrorMessage = ({ message = 'An error occurred.' }) => {
  return (
    <Flex
      align="center"
      justify="center"
      h="100%"
    >
      <Box
        maxW="md"
        p={4}
        borderWidth={1}
        boxShadow="lg"
        bg="white"
      >
        <Alert status="error" borderRadius="md">
          <AlertIcon />
          <AlertDescription>
            <Text fontSize="md" fontWeight="bold">
              Error
            </Text>
            {message}
          </AlertDescription>
        </Alert>
      </Box>
    </Flex>
  );
};

export default ErrorMessage;
