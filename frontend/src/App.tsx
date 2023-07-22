import React from 'react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import PokemonsContextProvider from './contexts/PokemonsContext';
import HomePage from './pages/HomePage';

const client = new QueryClient();

function App() {
  return (
    <BrowserRouter>
      <QueryClientProvider client={client}>
        <PokemonsContextProvider>
          <Routes>
            <Route path='/' element={<HomePage />} />
          </Routes>
        </PokemonsContextProvider>
      </QueryClientProvider>
    </BrowserRouter>
  );
}

export default App;
