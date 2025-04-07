import React from 'react';
import { FlatList, StyleSheet, ViewStyle, StyleProp } from 'react-native';
import { ArtCrime } from '@/api/artCrimesApi';
import { spacing } from '@/config/theme';
import GalleryCard from './galleryCard';
import LoadingOverlay from '../loadingOverlay';

const numColumns = 2;

type GalleryListProps = {
  items: ArtCrime[];
  isFetchingNextPage: boolean;
  onEndReached: () => void;
  style?: StyleProp<ViewStyle>;
  ListEmptyComponent?: React.ComponentType<any> | React.ReactElement | null;
};

export default function InfinityGalleryList({
  items,
  isFetchingNextPage,
  style,
  onEndReached,
  ListEmptyComponent,
}: GalleryListProps) {
  return (
    <FlatList
      data={items}
      keyExtractor={(item, index) => item.uid || `art-${index}`} // Note: all item properties might be null (from the online API schema)
      numColumns={numColumns}
      contentContainerStyle={[styles.list, style]}
      columnWrapperStyle={styles.columnWrapper}
      onEndReached={onEndReached}
      onEndReachedThreshold={0.5}
      renderItem={({ item }) => <GalleryCard item={item} onPress={() => {}} />}
      ListEmptyComponent={ListEmptyComponent}
      ListFooterComponent={isFetchingNextPage ? <LoadingOverlay fullScreen={false} /> : null}
    />
  );
}

const styles = StyleSheet.create({
  list: {
    paddingVertical: spacing.lg,
  },
  columnWrapper: {
    justifyContent: 'space-between',
    marginBottom: spacing.xs,
  },
});
