import { ThemeProvider } from './context/themeContext';
import NavigationWrapper from './navigation/navigationWrapper';

export default function App() {
  return (
    <ThemeProvider>
      <NavigationWrapper />
    </ThemeProvider>
  );
}
