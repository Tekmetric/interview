import React from 'react'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { Container } from '@mui/material'
import PokemonsList from './components/PokemonsList'

const client = new QueryClient()

function App() {
  return (
    <QueryClientProvider client={client}>
      <Container className='bg-blue-100 shadow-blue-400 shadow-xl p-10 m-10 min-h-screen h-full '>
        <PokemonsList />
      </Container>
    </QueryClientProvider>
  )
}

export default App
