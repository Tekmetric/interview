import { ThemeProvider } from './context/themeContext';
import NavigationWrapper from './navigation/navigationWrapper';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { SavedItemsProvider } from './context/savedItemsContext';

const queryClient = new QueryClient();

export default function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <ThemeProvider>
        <SavedItemsProvider>
          <NavigationWrapper />
        </SavedItemsProvider>
      </ThemeProvider>
    </QueryClientProvider>
  );
}
