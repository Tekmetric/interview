import React from 'react';
import LandingPage from './pages/LandingPage';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import IndividualAnimePage from './pages/IndividualAnimePage';
import { AnimeListPage } from './pages/AnimeListPage';
import { Header } from './components/Header';
import { Footer } from './components/Footer';

const queryClient = new QueryClient();

const App = () => {
  return (
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <Header />
          <div className='container m-auto my-6 min-h-[calc(100vh-224px)]'>
            <Routes>
              <Route path="/top/:filter" element={<AnimeListPage />} />
              <Route path="/:id" element={<IndividualAnimePage />} />
              <Route path="/" element={<LandingPage />} />
            </Routes>
          </div>
        <Footer />
      </BrowserRouter>
    </QueryClientProvider>
  );
}

export default App;
