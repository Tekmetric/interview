import React from 'react';
import { ThemeProvider } from 'styled-components';
import { Provider } from 'react-redux';

import { lightTheme } from './theme';
import { store } from './store/store';
import NavigationBar from './components/NavigationBar/NavigationBar';
import Routes from './routes/Routes';
import { AppContainer } from './App.styled';

function App() {
  return (
    <Provider store={store}>
      <ThemeProvider theme={lightTheme}>
        <AppContainer style={{ backgroundImage: "url('/images/anime_background.jpg')" }}>
          <NavigationBar />
          <Routes />
        </AppContainer>
      </ThemeProvider>
    </Provider>
  );
}

export default App;
