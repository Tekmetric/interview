import { getLiveData } from '@/api/parks';
import ErrorDisplay from '@/components/ErrorDisplay';
import RideWaitTimeList from '@/components/RideWaitTimeList';
import { Skeleton } from '@/components/ui/skeleton';
import type { EntityLiveDataResponse, LiveDataParams } from '@/types/parks-api';
import { useQuery } from '@tanstack/react-query';
import { createFileRoute } from '@tanstack/react-router';

export const Route = createFileRoute('/parks/$parkId')({
  beforeLoad: (context) => {
    const parkId = context.params.parkId;
    const entityType = 'ATTRACTION';
    const params: LiveDataParams = { parkId, entityType };
    return {
      parkQueryOptions: { queryKey: ['liveData', params], queryFn: () => getLiveData(params) },
    };
  },
  loader: async ({ context: { queryClient, parkQueryOptions } }) => {
    queryClient.prefetchQuery(parkQueryOptions);
  },
  component: ParkLiveData,
});

function ParkLiveData() {
  const { parkQueryOptions } = Route.useRouteContext();
  const { data, error, isPending } = useQuery<EntityLiveDataResponse>({
    ...parkQueryOptions,
    refetchInterval: 5 * 60 * 1000, // 5 minutes
    retry: (failureCount, error) => {
      if (error.status === 404) return false;
      if (failureCount >= 3) return false;
      return true;
    },
  });

  return (
    <div className="max-w-2xl mx-auto px-4 py-8">
      {isPending && (
        <>
          <Skeleton className="h-12 w-3/4 mb-4" />
          <Skeleton className="h-6 w-3/4 mb-6" />
          <div className="flex flex-col gap-4">
            <Skeleton className="w-full h-[120px]" />
            <Skeleton className="w-full h-[120px]" />
            <Skeleton className="w-full h-[120px]" />
            <Skeleton className="w-full h-[120px]" />
            <Skeleton className="w-full h-[120px]" />
            <Skeleton className="w-full h-[120px]" />
          </div>
        </>
      )}
      {error && <ErrorDisplay error={error} />}
      {data && (
        <>
          <h1 className="text-3xl font-bold mb-6">{data.name}</h1>
          <RideWaitTimeList liveData={data.liveData} timezone={data.timezone} />
        </>
      )}
    </div>
  );
}
