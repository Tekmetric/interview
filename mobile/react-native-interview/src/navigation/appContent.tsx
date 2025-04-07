import { NavigationContainer } from '@react-navigation/native';
import { useTheme } from '../context/themeContext';
import RootNavigator from './rootNavigator';
import { StatusBar } from 'react-native';

export default function AppContent() {
  const { theme, mode } = useTheme();

  return (
    <NavigationContainer theme={theme}>
      <StatusBar
        barStyle={mode === 'dark' ? 'light-content' : 'dark-content'}
        backgroundColor="transparent"
        translucent
      />
      <RootNavigator />
    </NavigationContainer>
  );
}
