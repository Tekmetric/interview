import './App.css';
import AppRoutes from './routes/AppRoutes.tsx';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import '@fontsource/roboto';
import '@fontsource/roboto/500.css';
import '@fontsource/roboto/700.css';
import { FilterProvider } from './providers/FilterProvider.tsx';
import { CartProvider } from './providers/CartProvider.tsx';

const queryClient = new QueryClient();

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <FilterProvider>
        <CartProvider>
          <AppRoutes />
        </CartProvider>
      </FilterProvider>
    </QueryClientProvider>
  );
}

export default App;
