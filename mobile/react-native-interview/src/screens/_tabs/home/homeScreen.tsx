import { View, Text, StyleSheet } from 'react-native';
import { useNavigation } from '@react-navigation/native';
import { useCallback, useLayoutEffect, useMemo, useState } from 'react';
import type { NativeStackNavigationProp } from '@react-navigation/native-stack';
import { RootStackParamList } from '../../../navigation/rootNavigator';
import useFetchArtCrimes from '../../../hooks/useFetchArtCrimes';
import FilterModal from '../../../components/modals/filterModal';
import FilterHeaderButton from '../../../components/buttons/filterHeaderButton';
import { ArtCrimeFilters, ArtCrimeQueryParams, ArtCrimeSorting } from '../../../types/artCrime';
import DebouncedSearchInput from '../../../components/inputs/debouncedSearchInputText';

export default function HomeScreen() {
  const navigation = useNavigation<NativeStackNavigationProp<RootStackParamList>>();
  const [filters, setFilters] = useState<ArtCrimeFilters>({});
  const [sort, setSort] = useState<ArtCrimeSorting>({ sort_on: 'modified', sort_order: 'desc' });
  const [showFilterModal, setShowFilterModal] = useState(false);

  const [debouncedSearch, setDebouncedSearch] = useState('');

  const toogleFilterModal = useCallback(() => {
    setShowFilterModal((value) => !value);
  }, []);

  useLayoutEffect(() => {
    navigation.setOptions({
      headerLeft: () => <FilterHeaderButton onPress={toogleFilterModal} filters={filters} />,
    });
  }, [navigation, toogleFilterModal, filters]);

  const artCrimesParams: ArtCrimeQueryParams = useMemo(() => {
    return {
      ...(debouncedSearch && { title: debouncedSearch }),
      ...filters,
      ...sort,
    };
  }, [debouncedSearch, filters, sort]);

  const { data, isLoading, error } = useFetchArtCrimes(artCrimesParams);

  return (
    <View style={styles.container}>
      <DebouncedSearchInput onDebouncedChange={setDebouncedSearch} placeholder="Search" />

      {isLoading && <Text style={{ color: 'blue' }}>Loading...</Text>}
      {error && <Text style={{ color: 'red' }}>{error.message}</Text>}

      {data?.items.map((item) => {
        return (
          <View key={item.uid}>
            <Text>{item.title}</Text>
          </View>
        );
      })}
      <FilterModal
        isVisible={showFilterModal}
        filters={filters}
        onRequestClose={() => setShowFilterModal(false)}
        setFilters={setFilters}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
});
