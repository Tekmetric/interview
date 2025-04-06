import { ThemeProvider } from './context/themeContext';
import NavigationWrapper from './navigation/navigationWrapper';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';

const queryClient = new QueryClient();

export default function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <ThemeProvider>
        <NavigationWrapper />
      </ThemeProvider>
    </QueryClientProvider>
  );
}
