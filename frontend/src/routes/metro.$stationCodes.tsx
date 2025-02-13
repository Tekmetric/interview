import { metroArrivalTimesOptions } from '@api/wmata';
import { METRO_COLORS } from '@constants/wmata';
import { Train } from '@phosphor-icons/react';
import { useSuspenseQuery } from '@tanstack/react-query';
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

function MetroStationCodes() {
  const { stationCodes } = Route.useParams();
  const { line } = Route.useSearch();
  const metroArrivalTimesQuery = useSuspenseQuery(metroArrivalTimesOptions(stationCodes));
  const trains = metroArrivalTimesQuery.data.Trains.filter(train => train.Line && train.Line !== 'No' && train.Min && train.Min !== '---');
  const filteredTrains = line ? trains.filter(train => train.Line === line) : trains;

  return (
    <div>
      <table className="w-1/2 table-auto border-collapse text-sm">
        <tbody>
          { filteredTrains.map(train => (
            <tr key={ `${ train.Line }-${ train.LocationCode }-${ train.DestinationCode }-${ train.Min }-${ train.Group }` }>
              <td>
                At {train.LocationName}
              </td>
              <td className="flex align-center">
                <Train size="24" className={ `${ METRO_COLORS[train.Line as keyof typeof METRO_COLORS] } rounded` } />
                <div>
                  { train.Line } Line to { train.DestinationName }
                </div>
              </td>
              <td>
                { `${ train.Min } ${ Number.isNaN(Number(train.Min)) ? '' : ` min${ train.Min === '1' ? '' : 's' }` }` }
              </td>
            </tr>
          )) }
        </tbody>
      </table>
    </div>
  );
}
