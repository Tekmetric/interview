import React from 'react';
import { FlatList, Text, StyleSheet } from 'react-native';
import { ArtCrime } from '@/api/artCrimesApi';
import { spacing } from '@/config/theme';
import GalleryCard from './galleryCard';
import LoadingOverlay from '../loadingOverlay';

const numColumns = 2;

type GalleryListProps = {
  items: ArtCrime[];
  isFetchingNextPage: boolean;
  onEndReached: () => void;
};
export default function InfinityGalleryList({
  items,
  isFetchingNextPage,
  onEndReached,
}: GalleryListProps) {
  return (
    <FlatList
      data={items}
      keyExtractor={(item, index) => item.uid || `art-${index}`} // Note: all item properties might be null (from the online API schema)
      numColumns={numColumns}
      contentContainerStyle={styles.list}
      columnWrapperStyle={styles.columnWrapper}
      onEndReached={onEndReached}
      onEndReachedThreshold={0.5}
      renderItem={({ item }) => <GalleryCard item={item} onPress={() => {}} />}
      ListEmptyComponent={
        <Text>No items found, try to clear the search and filters to see all items</Text>
      }
      ListFooterComponent={isFetchingNextPage ? <LoadingOverlay fullScreen={false} /> : null}
    />
  );
}

const styles = StyleSheet.create({
  list: {
    paddingBottom: spacing.md,
    paddingTop: 60,
  },
  columnWrapper: {
    justifyContent: 'space-between',
    marginBottom: spacing.xs,
  },
});
