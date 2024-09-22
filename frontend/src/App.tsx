import React from 'react'
import './App.css'
import AppRoutes from './routes/AppRoutes';
import { store } from "./store/app.store";
import { Provider } from 'react-redux';

function App() {
  return (
    <>
      <Provider store={store}>
        <AppRoutes />
      </Provider>
    </>
  );
};

export default App;
