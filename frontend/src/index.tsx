import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import '@fontsource/roboto/300.css';
import '@fontsource/roboto/400.css';
import '@fontsource/roboto/500.css';
import '@fontsource/roboto/700.css';
import App from './App'; 
import { ChakraProvider } from '@chakra-ui/react';
import { Toaster } from 'react-hot-toast';

let root = ReactDOM.createRoot(document.getElementById('root') as HTMLElement);

root.render(
  <React.StrictMode>
    <ChakraProvider>
      <App />
      <Toaster
        position="bottom-right"
        reverseOrder={false}
      />
    </ChakraProvider>
  </React.StrictMode>,
);