import { getLiveData } from '@/api/parks';
import ParkPreview from '@/components/ParkPreview';
import { WALT_DISNEY_ENTITY_ID } from '@/constants';
import type { EntityLiveDataResponse, LiveDataParams } from '@/types/parks-api';
import { createFileRoute } from '@tanstack/react-router';
import { useQuery } from '@tanstack/react-query';
import ErrorDisplay from '@/components/ErrorDisplay';
import { Skeleton } from '@/components/ui/skeleton';

const params: LiveDataParams = { parkId: WALT_DISNEY_ENTITY_ID, entityType: 'PARK' };

export const Route = createFileRoute('/')({
  beforeLoad: () => {
    return {
      parkQueryOptions: { queryKey: ['liveData', params], queryFn: () => getLiveData(params) },
    };
  },
  loader: async ({ context: { queryClient, parkQueryOptions } }) => {
    queryClient.prefetchQuery(parkQueryOptions);
  },
  component: App,
});

function App() {
  const { parkQueryOptions } = Route.useRouteContext();
  const { data, error, isPending } = useQuery<EntityLiveDataResponse>(parkQueryOptions);

  return (
    <div className="max-w-2xl mx-auto px-4 py-8">
      {isPending && (
        <>
          <Skeleton className="h-12 w-3/4 mb-4" />
          <Skeleton className="h-6 w-3/4 mb-6" />
          <div className="grid md:grid-cols-2 gap-4 justify-stretch">
            <Skeleton className="w-[382px] md:w-[312px] h-[132px]" />
            <Skeleton className="w-[382px] md:w-[312px] h-[132px]" />
            <Skeleton className="w-[382px] md:w-[312px] h-[132px]" />
            <Skeleton className="w-[382px] md:w-[312px] h-[132px]" />
            <Skeleton className="w-[382px] md:w-[312px] h-[132px]" />
            <Skeleton className="w-[382px] md:w-[312px] h-[132px]" />
          </div>
        </>
      )}
      {error && <ErrorDisplay error={error} showLink={false} />}
      {data && (
        <>
          <h1 className="text-3xl font-bold">Wait times at Walt Disney World Resort &reg;</h1>
          <p className="mt-4 mb-6">Select a park below for live updates on ride wait times!</p>
          <div className="grid md:grid-cols-2 gap-4 justify-stretch">
            {data.liveData && (
              <>
                {data.liveData.length > 0 && data.liveData.map((park) => <ParkPreview key={park.id} park={park} />)}
                {data.liveData.length === 0 && <p className="text-center p-10 text-lg">No park data available.</p>}
              </>
            )}
          </div>
        </>
      )}
    </div>
  );
}
