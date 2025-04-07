import { View, StyleSheet } from 'react-native';
import { ArtCrimeQueryParams } from '@/types/artCrime';
import LoadingOverlay from '@/components/loadingOverlay';
import InfinityGalleryList from '@/components/gallery/infinityGalleryList';
import { getErrorMessage } from '@/utils/getErrorMessage';
import InlineError from '@/components/inlineError';
import { useArtCrimesList } from '@/hooks/useArtCrimesList';
import EmptyState from '@/components/gallery/emptyState';

type HomeScreenContentProps = {
  queryParams: ArtCrimeQueryParams;
};

export default function HomeScreenContent({ queryParams }: HomeScreenContentProps) {
  const { items, isLoading, error, isFetchingNextPage, loadMore, refetch } = useArtCrimesList({
    queryParams,
  });

  if (isLoading) return <LoadingOverlay />;
  if (error)
    return (
      <InlineError message={getErrorMessage(error)} title="Opss..." onRetry={() => refetch()} />
    );
  return (
    <View style={styles.container}>
      <InfinityGalleryList
        items={items || []}
        isFetchingNextPage={isFetchingNextPage}
        onEndReached={loadMore}
        style={styles.galleryList}
        ListEmptyComponent={
          <EmptyState
            title="No items found"
            description="Try to clear the search and filters to see all items"
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
  galleryList: {
    paddingTop: 50,
  },
});
