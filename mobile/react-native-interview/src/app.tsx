import React from 'react';
import { ThemeProvider } from './context/themeContext';
import AppContent from './navigation/appContent';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { SavedItemsProvider } from './context/savedItemsContext';

const queryClient = new QueryClient();

export default function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <ThemeProvider>
        <SavedItemsProvider>
          <AppContent />
        </SavedItemsProvider>
      </ThemeProvider>
    </QueryClientProvider>
  );
}
