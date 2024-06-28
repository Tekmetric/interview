import React from 'react';
import BookTable from './components/BookTable';
import { BooksProvider } from './context/BooksContext';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { QueryClient, QueryClientProvider } from 'react-query';

// Create a client
const queryClient = new QueryClient();

const App: React.FC = () => {
  return (
    <QueryClientProvider client={queryClient}>
      <BooksProvider>
        <div className="App">
          <div className='flex flex-row'>
            <div className='w-1/3'>
            <img src={`${process.env.PUBLIC_URL}/tekmetric.jpg`} alt="Tekmetric Logo" className="h-14 mr-4" />
            </div>
          
          <h1 className="w-1/3 text-3xl font-bold mb-4 mt-4 text-center justify-center">The Book Bazaar</h1>
          </div>
  
          <BookTable />
          <ToastContainer />
        </div>
      </BooksProvider>
    </QueryClientProvider>
  );
};

export default App;
