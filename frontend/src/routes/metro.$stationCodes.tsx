import { metroArrivalTimesOptions } from '@api/wmata';
import { MetroStationCodes } from '@components/routes/MetroStationCodes';
import { createFileRoute } from '@tanstack/react-router';
import { z } from 'zod';

export interface StationCodeSearch {
  line?: string;
}

export const Route = createFileRoute('/metro/$stationCodes')({
  component: MetroStationCodes,
  params: {
    parse: ({ stationCodes }) => ({
      stationCodes: z.string().parse(`${ stationCodes }`)
    })
  },
  validateSearch: (search: Record<string, unknown>): StationCodeSearch => {
    return {
      line: search?.line as string
    };
  },
  loader: async ({ context, params }) => {
    context.queryClient.ensureQueryData(
      metroArrivalTimesOptions(params.stationCodes)
    );
  },
  pendingComponent: () => 'Loading...'
});
