import { Header, Footer, Content } from './components';
import ShareModal from './components/ShareModal';
import {
  SearchContextProvider,
  FavouritesContextProvider,
  ShareDialogContextProvider,
} from './state';

const App = () => (
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
);

export default App;
