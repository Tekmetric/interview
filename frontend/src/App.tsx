import { Header, Footer, Content } from './components';
import { SearchContextProvider } from './state/SearchContext';

const App = () => (
  <div className="font-mono App">
    <Header />
    <SearchContextProvider>
      <Content />
    </SearchContextProvider>
    <Footer />
  </div>
);

export default App;
