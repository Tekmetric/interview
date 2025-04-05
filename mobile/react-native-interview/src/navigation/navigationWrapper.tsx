import { NavigationContainer } from '@react-navigation/native';
import { useTheme } from '../context/themeContext';
import RootNavigator from './rootNavigator';

export default function NavigationWrapper() {
  const { theme } = useTheme();

  return (
    <NavigationContainer theme={theme}>
      <RootNavigator />
    </NavigationContainer>
  );
}
