import React from 'react';

import { Provider } from 'react-redux';
import { store } from './store/store';
import NavigationBar from './components/NavigationBar/NavigationBar';
import Routes from './routes/Routes';
import { AppContainer } from './App.styled';

function App() {
  return (
    <Provider store={store}>
      <AppContainer style={{ backgroundImage: "url('/images/anime_background.jpg')" }}>
        <NavigationBar />
        <Routes />
      </AppContainer>
    </Provider>
  );
}

export default App;
