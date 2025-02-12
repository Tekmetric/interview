import { metroArrivalTimesOptions } from '@api/wmata';
import { METRO_COLORS } from '@constants/wmata';
import { Train } from '@phosphor-icons/react';
import { useSuspenseQuery } from '@tanstack/react-query';
import { createFileRoute } from '@tanstack/react-router';
import { z } from 'zod';

export const Route = createFileRoute('/metro/$stationCodes')({
  component: MetroStationCodes,
  params: {
    parse: ({ stationCodes }) => ({
      stationCodes: z.string().parse(`${ stationCodes }`)
    })
  },
  loader: async ({ context, params }) => {
    context.queryClient.ensureQueryData(
      metroArrivalTimesOptions(params.stationCodes)
    );
  },
  pendingComponent: () => 'Loading...'
});

function MetroStationCodes() {
  const params = Route.useParams();
  const metroArrivalTimesQuery = useSuspenseQuery(metroArrivalTimesOptions(params.stationCodes));
  const trains = metroArrivalTimesQuery.data.Trains.filter(train => train.Line && train.Line !== 'No' && train.Min && train.Min !== '---');
  return (
    <div>
      <table className="w-1/2 table-auto border-collapse text-sm">
        <tbody>
          { trains.map(train => (
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
