import './App.css';
import AppRoutes from './routes/AppRoutes.tsx';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import '@fontsource/roboto'; // Defaults to weight 400

const queryClient = new QueryClient();

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <AppRoutes />
    </QueryClientProvider>
  );
}

export default App;
