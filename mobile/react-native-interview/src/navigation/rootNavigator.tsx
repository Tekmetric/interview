import { createNativeStackNavigator } from '@react-navigation/native-stack';
import TabsNavigator from './tabsNavigator';
import DetailsScreen from '../screens/details/detailsScreen';

export type RootStackParamList = {
  Tabs: undefined;
  Details: undefined;
};

const Stack = createNativeStackNavigator<RootStackParamList>();

export default function RootNavigator() {
  return (
    <Stack.Navigator>
      <Stack.Screen name="Tabs" component={TabsNavigator} options={{ headerShown: false }} />
      <Stack.Screen name="Details" component={DetailsScreen} />
    </Stack.Navigator>
  );
}
