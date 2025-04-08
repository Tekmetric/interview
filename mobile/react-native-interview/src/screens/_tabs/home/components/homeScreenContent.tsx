import { View, StyleSheet } from 'react-native';
import { ArtCrimeQueryParams } from '@/types/artCrime';
import LoadingOverlay from '@/components/loadingOverlay';
import GalleryList from '@/components/gallery/galleryList';
import { getErrorMessage } from '@/utils/getErrorMessage';
import InlineError from '@/components/inlineError';
import { useArtCrimesList } from '@/hooks/useArtCrimesList';
import EmptyState from '@/components/gallery/emptyState';

type HomeScreenContentProps = {
  queryParams: ArtCrimeQueryParams;
};

export default function HomeScreenContent({ queryParams }: HomeScreenContentProps) {
  const { items, isLoading, error, isFetchingNextPage, isRefetching, loadMore, refetch } =
    useArtCrimesList({
      queryParams,
    });

  // TODO: isRefetching loader is hidden behind the seach input, needs to be fixed.
  if (isLoading) return <LoadingOverlay />;
  if (error)
    return (
      <InlineError message={getErrorMessage(error)} title="Opss..." onRetry={() => refetch()} />
    );
  return (
    <View style={styles.container}>
      <GalleryList
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
        refetch={refetch}
        isRefetching={isRefetching}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  galleryList: {
    paddingTop: 60,
  },
});
