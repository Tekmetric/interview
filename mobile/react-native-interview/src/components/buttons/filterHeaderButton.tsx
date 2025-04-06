import { StyleSheet, View } from 'react-native';
import { ArtCrimeFilters } from '../../types/artCrime';
import { Feather } from '@expo/vector-icons';
import { useMemo } from 'react';
import HeaderButton from './headerButton';
import { useTheme } from '@react-navigation/native';

type FilterHeaderButtonProps = {
  filters: ArtCrimeFilters;
  onPress: () => void;
};

export default function FilterHeaderButton({ onPress, filters }: FilterHeaderButtonProps) {
  const hasFilters = useMemo(() => {
    return Object.values(filters).some((value) => value !== null);
  }, [filters]);
  const { colors } = useTheme();

  return (
    <HeaderButton onPress={onPress}>
      <Feather name="filter" size={24} color={colors.text} />
      {hasFilters && <View style={styles.activeFilterIndicator} />}
    </HeaderButton>
  );
}

const styles = StyleSheet.create({
  activeFilterIndicator: {
    position: 'absolute',
    top: -4,
    right: -4,
    width: 8,
    height: 8,
    borderRadius: 4,
    backgroundColor: 'red',
  },
});
