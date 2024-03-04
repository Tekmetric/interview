import { Header, Footer, Content } from './components';
import ShareModal from './components/ShareModal';
import {
  SearchContextProvider,
  FavouritesContextProvider,
  ShareDialogContextProvider,
} from './state';

import { createTheme, ThemeProvider } from '@mui/material/styles';

const theme = createTheme({
  palette: {
    primary: {
      main: '#67E8F9', // cyan-300
    },
    secondary: {
      main: '#0E7490', // cyan-700
    },
  },
});

const App = () => (
  <ThemeProvider theme={theme}>
    <div className="font-mono App">
      <ShareDialogContextProvider>
        <Header />
        <FavouritesContextProvider>
          <SearchContextProvider>
            <Content />
          </SearchContextProvider>
        </FavouritesContextProvider>
        <Footer />
        <ShareModal />
      </ShareDialogContextProvider>
    </div>
  </ThemeProvider>
);

export default App;
