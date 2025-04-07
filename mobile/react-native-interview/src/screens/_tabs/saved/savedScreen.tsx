import React from 'react';
import { View, StyleSheet } from 'react-native';
import { useSavedItems } from '@/context/savedItemsContext';
import LoadingOverlay from '@/components/loadingOverlay';
import InfinityGalleryList from '@/components/gallery/infinityGalleryList';
import EmptyState from '@/components/gallery/emptyState';

export default function SavedScreen() {
  const { savedItems, isLoading } = useSavedItems();

  if (isLoading) return <LoadingOverlay />;

  return (
    <View style={styles.container}>
      <InfinityGalleryList
        items={savedItems}
        isFetchingNextPage={false}
        onEndReached={() => {}}
        ListEmptyComponent={
          <EmptyState
            icon="bookmark"
            title="No saved items"
            description="Save items from the home screen to see them here. Tap the bookmark icon on any item to save it."
          />
        }
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
});
