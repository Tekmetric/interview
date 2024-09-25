import React from 'react'
import './App.css'
import AppRoutes from './routes/AppRoutes';
import { store } from "./store/app.store";
import { Provider } from 'react-redux';

const App: React.FC = () => {
  return (
    <>
      <Provider store={store}>
        <AppRoutes />
      </Provider>
    </>
  );
};

export default App;
