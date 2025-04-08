import React, { useState } from 'react';
import {
  View,
  Text,
  ScrollView,
  StyleSheet,
  Image,
  TouchableOpacity,
  ImageStyle,
} from 'react-native';
import { Feather } from '@expo/vector-icons';
import { useTheme } from '@/context/themeContext';
import { spacing, typography } from '@/config/theme';
import { NativeStackScreenProps } from '@react-navigation/native-stack';
import { RootStackParamList } from '@/navigation/rootNavigator';
import ImageModal from '@/components/modals/imageModal';
import { useSavedItems } from '@/context/savedItemsContext';
import ArtCrimeField from '@/components/details/artCrimeField';
import { useArtCrimeById } from '@/hooks/useArtCrimeById';
import LoadingOverlay from '@/components/loadingOverlay';
import InlineError from '@/components/inlineError';
import { getErrorMessage } from '@/utils/getErrorMessage';

type Props = NativeStackScreenProps<RootStackParamList, 'Details'>;

export default function ArtCrimeDetailsScreen({ route }: Props) {
  // Note: hackis way to send the entire item from the gallery list to fix the broken image urls from the API when we fetch a single item.
  const { item: initialItem } = route.params;
  const { theme } = useTheme();
  const { saveItem, removeItem, isItemSaved } = useSavedItems();
  const [isModalVisible, setIsModalVisible] = useState(false);

  const { data: item, isLoading, error, refetch } = useArtCrimeById(initialItem.uid);
  const isSaved = isItemSaved(initialItem.uid);

  const handleSavePress = () => {
    if (isSaved) {
      removeItem(initialItem.uid);
    } else {
      saveItem(initialItem);
    }
  };

  const handleImagePress = () => {
    setIsModalVisible(true);
  };

  if (isLoading) return <LoadingOverlay />;
  if (error)
    return <InlineError message={getErrorMessage(error)} title="Oops..." onRetry={refetch} />;
  if (!item) return null;

  return (
    <ScrollView style={[styles.container, { backgroundColor: theme.colors.background }]}>
      <TouchableOpacity onPress={handleImagePress} style={styles.imageContainer}>
        <Image
          source={{ uri: initialItem.images?.[0]?.original ?? '' }}
          style={styles.image as ImageStyle}
          resizeMode="cover"
        />
      </TouchableOpacity>

      <View style={styles.contentContainer}>
        <View style={styles.headerContainer}>
          <Text style={[styles.title, { color: theme.colors.text }]}>{item.title}</Text>
          <TouchableOpacity
            style={[styles.saveButton, { backgroundColor: theme.colors.card }]}
            onPress={handleSavePress}
          >
            <Feather
              name={isSaved ? 'bookmark' : 'bookmark'}
              size={24}
              color={isSaved ? theme.colors.primary : theme.colors.text}
            />
          </TouchableOpacity>
        </View>

        <ArtCrimeField label="Description" value={item.description} />
        <ArtCrimeField label="Crime Category" value={item.crimeCategory} />
        <ArtCrimeField label="Maker" value={item.maker} />
        <ArtCrimeField label="Materials" value={item.materials} />
        <ArtCrimeField label="Measurements" value={item.measurements} />
        <ArtCrimeField label="Period" value={item.period} />
        <ArtCrimeField label="Additional Data" value={item.additionalData} />
        <ArtCrimeField label="Reference Number" value={item.referenceNumber} />
      </View>

      <ImageModal
        visible={isModalVisible}
        onClose={() => setIsModalVisible(false)}
        uri={initialItem.images?.[0]?.original ?? ''}
      />
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  imageContainer: {
    width: '100%',
    height: 300,
  },
  image: {
    width: '100%',
    height: '100%',
  },
  contentContainer: {
    padding: spacing.lg,
  },
  headerContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: spacing.lg,
  },
  title: {
    fontSize: typography.fontSize.xl,
    fontWeight: '700',
    flex: 1,
    marginRight: spacing.md,
  },
  saveButton: {
    padding: spacing.sm,
    borderRadius: 8,
  },
});
