import { createNativeStackNavigator } from '@react-navigation/native-stack';
import TabsNavigator from './tabsNavigator';
import ArtCrimeDetailsScreen from '@/screens/details/artCrimeDetailsScreen';
import { ArtCrime } from '@/types/artCrime';

export type RootStackParamList = {
  Tabs: undefined;
  // Note: hackis way to fix the broken image urls from the API when we fetch a single item
  Details: { item: ArtCrime };
};

const Stack = createNativeStackNavigator<RootStackParamList>();

export default function RootNavigator() {
  return (
    <Stack.Navigator>
      <Stack.Screen name="Tabs" component={TabsNavigator} options={{ headerShown: false }} />
      <Stack.Screen
        name="Details"
        component={ArtCrimeDetailsScreen}
        options={{ title: 'Art Crime Details' }}
      />
    </Stack.Navigator>
  );
}
