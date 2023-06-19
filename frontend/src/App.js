import React from 'react';
import { ChakraProvider } from "@chakra-ui/react";
import StoreContainer from "./components/storeContainer/StoreContainer";
import { ProductsProvider } from "./context/ProductsContext";
import customTheme from "./theme";

const App = () => {
  return (
    <ChakraProvider theme={customTheme}>
      <ProductsProvider>
        <StoreContainer/>
      </ProductsProvider>
    </ChakraProvider>
  );
}

export default App;
