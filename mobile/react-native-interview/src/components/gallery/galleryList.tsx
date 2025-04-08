import React from 'react';
import { FlatList, StyleSheet, ViewStyle, StyleProp } from 'react-native';
import { spacing } from '@/config/theme';
import { ArtCrime } from '@/types/artCrime';
import LoadingOverlay from '../loadingOverlay';
import GalleryCard from './galleryCard';
import { useNavigation } from '@react-navigation/native';
import { NativeStackNavigationProp } from '@react-navigation/native-stack';
import { RootStackParamList } from '@/navigation/rootNavigator';

const numColumns = 2;

type GalleryListProps = {
  items: ArtCrime[];
  isFetchingNextPage: boolean;
  onEndReached: () => void;
  style?: StyleProp<ViewStyle>;
  ListEmptyComponent?: React.ComponentType<any> | React.ReactElement | null;
};

export default function GalleryList({
  items,
  isFetchingNextPage,
  style,
  onEndReached,
  ListEmptyComponent,
}: GalleryListProps) {
  const navigation = useNavigation<NativeStackNavigationProp<RootStackParamList>>();

  const handleCardPress = (item: ArtCrime) => {
    navigation.navigate('Details', { item });
  };

  return (
    <FlatList
      data={items}
      keyExtractor={(item, index) => item.uid || `art-${index}`} // Note: all item properties might be null (from the online API schema)
      numColumns={numColumns}
      contentContainerStyle={[styles.list, style]}
      columnWrapperStyle={styles.columnWrapper}
      onEndReached={onEndReached}
      onEndReachedThreshold={0.5}
      renderItem={({ item }) => <GalleryCard item={item} onPress={() => handleCardPress(item)} />}
      ListEmptyComponent={ListEmptyComponent}
      ListFooterComponent={isFetchingNextPage ? <LoadingOverlay fullScreen={false} /> : null}
    />
  );
}

const styles = StyleSheet.create({
  list: {
    padding: spacing.sm,
  },
  columnWrapper: {
    justifyContent: 'space-between',
    marginBottom: spacing.sm,
  },
});
