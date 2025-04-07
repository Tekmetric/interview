import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { Feather } from '@expo/vector-icons';
import HomeScreen from '../screens/_tabs/home/homeScreen';
import SavedScreen from '../screens/_tabs/saved/savedScreen';
import ModeSwitcher from './components/modeSwitcher';
import { useTheme } from '@/context/themeContext';

const Tab = createBottomTabNavigator();

export default function TabsNavigator() {
  const { theme } = useTheme();

  return (
    <Tab.Navigator
      screenOptions={{
        headerRight: () => <ModeSwitcher />,
        tabBarActiveTintColor: theme.colors.primary,
        tabBarInactiveTintColor: theme.colors.text,
      }}
    >
      <Tab.Screen
        name="home"
        component={HomeScreen}
        options={{
          title: 'Art Crimes',
          tabBarLabel: 'Home',
          tabBarIcon: ({ color, size }) => <Feather name="home" size={size} color={color} />,
        }}
      />
      <Tab.Screen
        name="saved"
        component={SavedScreen}
        options={{
          title: 'My Saved Cases',
          tabBarLabel: 'Saved Cases',
          tabBarIcon: ({ color, size }) => <Feather name="bookmark" size={size} color={color} />,
        }}
      />
    </Tab.Navigator>
  );
}
