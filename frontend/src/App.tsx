import './App.css';
import AppRoutes from './routes/AppRoutes.tsx';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import '@fontsource/roboto';
import { FilterProvider } from './providers/FilterProvider.tsx';

const queryClient = new QueryClient();

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <FilterProvider>
        <AppRoutes />
      </FilterProvider>
    </QueryClientProvider>
  );
}

export default App;
