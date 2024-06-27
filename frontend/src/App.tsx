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
          <h1 className="text-3xl font-bold mb-4">Book List</h1>
          <BookTable />
          <ToastContainer />
        </div>
      </BooksProvider>
    </QueryClientProvider>
  );
};

export default App;
