import React from 'react';
//import './App.css';
import BookTable from './components/BookTable';
import { BooksProvider } from './context/BooksContext';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

const App: React.FC = () => {
  return (
    <BooksProvider>
      <div className="App">
        <h1 className="text-3xl font-bold mb-4">Book List</h1>
        <BookTable />
        <ToastContainer />
      </div>
    </BooksProvider>
  );
};

export default App;
