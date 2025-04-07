import { useNavigation } from '@react-navigation/native';
import { useLayoutEffect } from 'react';
import type { NativeStackNavigationProp } from '@react-navigation/native-stack';
import { RootStackParamList } from '@/navigation/rootNavigator';
import FilterModal from '@/components/modals/filterModal';
import FilterHeaderButton from '@/components/buttons/filterHeaderButton';
import DebouncedSearchInput from '@/components/inputs/debouncedSearchInputText';
import { useArtCrimeFilters } from '@/hooks/useArtCrimeFilters';
import { useFilterModal } from '@/hooks/useFilterModal';
import SafeAreaWrapper from '@/components/safeAreaWrapper';
import HomeScreenContent from './components/homeScreenContent';

export default function HomeScreen() {
  const navigation = useNavigation<NativeStackNavigationProp<RootStackParamList>>();
  const { filters, setFilters, setDebouncedSearch, artCrimesParams } = useArtCrimeFilters();
  const { showFilterModal, toggleFilterModal, closeFilterModal } = useFilterModal();

  useLayoutEffect(() => {
    navigation.setOptions({
      headerLeft: () => <FilterHeaderButton onPress={toggleFilterModal} filters={filters} />,
    });
  }, [navigation, toggleFilterModal, filters]);

  return (
    <SafeAreaWrapper>
      <DebouncedSearchInput onDebouncedChange={setDebouncedSearch} placeholder="Search titles..." />
      <HomeScreenContent queryParams={artCrimesParams} />
      <FilterModal
        isVisible={showFilterModal}
        filters={filters}
        onRequestClose={closeFilterModal}
        setFilters={setFilters}
      />
    </SafeAreaWrapper>
  );
}
