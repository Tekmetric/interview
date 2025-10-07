import React from 'react';

import { Provider } from 'react-redux';
import { store } from './store/store';
import NavigationBar from './components/NavigationBar/NavigationBar';
import Routes from './routes/Routes';

function App() {
  return (
    <Provider store={store}>
      <div style={{
        backgroundImage: "url('/images/anime_background.jpg')",
        backgroundRepeat: 'no-repeat',
        minHeight: '100vh',
        backgroundSize: 'cover',
        margin: -8,
        opacity: 0.7,
      }}
      >
        <NavigationBar />
        <Routes />
      </div>
    </Provider>
  );
}

export default App;
