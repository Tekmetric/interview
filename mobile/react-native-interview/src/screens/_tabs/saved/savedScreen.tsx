import React from 'react';
import { useSavedItems } from '@/context/savedItemsContext';
import LoadingOverlay from '@/components/loadingOverlay';
import EmptyState from '@/components/gallery/emptyState';
import SafeAreaWrapper from '@/components/safeAreaWrapper';
import GalleryList from '@/components/gallery/galleryList';

export default function SavedScreen() {
  const { savedItems, isLoading } = useSavedItems();

  if (isLoading) return <LoadingOverlay />;

  return (
    <SafeAreaWrapper>
      <GalleryList
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
    </SafeAreaWrapper>
  );
}
