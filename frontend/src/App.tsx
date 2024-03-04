import { Header, Footer, Content } from './components';
import { SearchContextProvider, FavouritesContextProvider } from './state';

const App = () => (
  <div className="font-mono App">
    <Header />
    <FavouritesContextProvider>
      <SearchContextProvider>
        <Content />
      </SearchContextProvider>
    </FavouritesContextProvider>
    <Footer />
  </div>
);

export default App;
