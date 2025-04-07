import { Modal, View, Text, StyleSheet, Pressable } from 'react-native';
import { Feather } from '@expo/vector-icons';
import { ArtCrimeFilters } from '../../types/artCrime';
import FilterInput from '../inputs/filterInput';
import { useState } from 'react';
import { spacing, typography } from '../../config/theme';
import { useTheme } from '@/context/themeContext';

type FilterModalProps = {
  isVisible: boolean;
  filters: ArtCrimeFilters;
  onRequestClose: () => void;
  setFilters: (filters: ArtCrimeFilters) => void;
};

type ArtCrimeFiltersItems = {
  [key in keyof ArtCrimeFilters]: {
    label: string;
    placeholder: string;
  };
};

const artCrimeFiltersItems: ArtCrimeFiltersItems = {
  crimeCategory: {
    label: 'Crime Category',
    placeholder: 'Select crime category',
  },
  maker: {
    label: 'Maker',
    placeholder: 'Select maker',
  },
  materials: {
    label: 'Materials',
    placeholder: 'Select materials',
  },
  period: {
    label: 'Period',
    placeholder: 'Select period',
  },
  idInAgency: {
    label: 'ID in Agency',
    placeholder: 'ID in Agency',
  },
  referenceNumber: {
    label: 'Reference Number',
    placeholder: 'Reference Number',
  },
  measurements: {
    label: 'Measurements',
    placeholder: 'Measurements',
  },
  additionalData: {
    label: 'Additional Data',
    placeholder: 'Additional Data',
  },
};

export default function FilterModal({
  isVisible,
  filters,
  onRequestClose,
  setFilters,
}: FilterModalProps) {
  const [modalFilters, setModalFilters] = useState(filters);
  const { theme } = useTheme();

  const updateModalFiterValue = (key: keyof ArtCrimeFilters, value: any) => {
    setModalFilters((prev) => {
      return {
        ...prev,
        [key]: value,
      };
    });
  };

  const onResetFilters = () => {
    setModalFilters({});
    setFilters({});
    onRequestClose();
  };

  const onSearchPress = () => {
    setFilters(modalFilters);
    onRequestClose();
  };

  return (
    <Modal
      visible={isVisible}
      animationType="slide"
      transparent={true}
      onRequestClose={onRequestClose}
    >
      <Pressable style={styles.modalOverlay} onPress={onRequestClose}>
        <Pressable style={styles.modalContent}>
          <View style={styles.modalHeader}>
            <Pressable onPress={onResetFilters} style={styles.headerButton}>
              <Feather name="refresh-ccw" size={24} color="black" />
              <Text>Reset</Text>
            </Pressable>
            <Text style={styles.title}>Filters</Text>
            <Pressable style={styles.headerButton} onPress={onRequestClose}>
              <Feather name="x" size={24} color="black" />
              <Text>Close</Text>
            </Pressable>
          </View>
          <View style={styles.modalContent}>
            {Object.entries(artCrimeFiltersItems).map(([key, value]) => {
              return (
                <FilterInput
                  key={key}
                  label={value.label}
                  value={modalFilters[key as keyof ArtCrimeFilters] ?? null}
                  placehoder={value.placeholder}
                  onChange={(value) => updateModalFiterValue(key as keyof ArtCrimeFilters, value)}
                />
              );
            })}
          </View>
          <Pressable
            style={[styles.searchButton, { backgroundColor: theme.colors.primary }]}
            onPress={onSearchPress}
          >
            <Feather name="save" size={24} color={theme.colors.text} />
            <Text style={[styles.searchButtonText, { color: theme.colors.text }]}>Save</Text>
          </Pressable>
        </Pressable>
      </Pressable>
    </Modal>
  );
}

const styles = StyleSheet.create({
  modalOverlay: {
    flex: 1,
    justifyContent: 'flex-end',
    alignItems: 'center',
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
  },
  title: {
    fontSize: typography.fontSize.lg,
    fontFamily: 'bold',
    textAlign: 'center',
  },
  modalHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    width: '100%',
    marginBottom: spacing.sm,
    padding: spacing.sm,
    borderTopLeftRadius: spacing.sm,
    borderTopRightRadius: spacing.sm,
  },
  headerButton: {
    display: 'flex',
    flexDirection: 'column',
    justifyContent: 'center',
    alignItems: 'center',
    padding: spacing.sm,
  },
  modalContent: {
    backgroundColor: 'white',
    borderRadius: 10,
    alignItems: 'center',
    width: '100%',
    minHeight: 200,
    padding: spacing.sm,
  },
  searchButton: {
    padding: spacing.sm,
    borderRadius: spacing.xl,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    width: '80%',
    margin: spacing.sm,
    marginBottom: spacing.xl,
  },
  searchButtonText: {
    marginLeft: spacing.sm,
    fontSize: typography.fontSize.lg,
    fontFamily: 'bold',
  },
});
