import { metroArrivalTimesOptions } from '@api/wmata';
import { MetroLineSelect } from '@components/MetroLineSelect';
import { ALL_STATIONS, METRO_COLORS, METRO_LINE_LABELS } from '@constants/wmata';
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
  const { line: lineFilter } = Route.useSearch();
  const currentStation = ALL_STATIONS.find(station => station.Code === stationCodes);
  const linesServedAtStation = currentStation ? [currentStation.LineCode1, currentStation.LineCode2, currentStation.LineCode3, currentStation.LineCode4, currentStation.LineCode5].filter(station => !!station) as string[] : [];

  const metroArrivalTimesQuery = useSuspenseQuery(metroArrivalTimesOptions(stationCodes));

  let upcomingTrains = metroArrivalTimesQuery.data.Trains.filter(train => train.Line && train.Line !== 'No' && train.Min && train.Min !== '---');

  if (lineFilter) {
    upcomingTrains = upcomingTrains.filter(train => train.Line === lineFilter);
  }

  if (lineFilter || linesServedAtStation.length === 1) {
    upcomingTrains.sort((a, b) => a.Destination.localeCompare(b.Destination));
  }

  return (
    !currentStation || metroArrivalTimesQuery.error
      ? 'Station not found :('
      : (
          <div className="mx-auto w-full">
            <h2 className="text-3xl text-center pt-2 font-bold">{currentStation.Name}</h2>
            {linesServedAtStation.length > 1 && <MetroLineSelect className="justify-center py-2" stationCodes={ stationCodes } lines={ linesServedAtStation } />}
            <table className="mx-auto w-1/2 table-auto border-separate border-spacing-1.5 text-sm">
              <tbody>
                { upcomingTrains.length > 0
                  ? upcomingTrains.map(train => (
                      <tr className="my-2" key={ `${ train.Line }-${ train.LocationCode }-${ train.Destination }-${ train.Min }-${ train.Group }` }>
                        <td className="flex align-center">
                          <Train size="24" className={ `${ METRO_COLORS[train.Line as keyof typeof METRO_COLORS] } rounded mr-2` } />
                          <div>
                            { METRO_LINE_LABELS[train.Line as keyof typeof METRO_LINE_LABELS] } Line to { train.DestinationName ?? train.Destination }
                          </div>
                        </td>
                        <td>
                          { `${ train.Min } ${ Number.isNaN(Number(train.Min)) ? '' : ` min${ train.Min === '1' ? '' : 's' }` }` }
                        </td>
                      </tr>
                    ))
                  : (
                      <tr>
                        <td className="text-center">No trains currently scheduled.</td>
                      </tr>
                    ) }
              </tbody>
            </table>
          </div>
        )
  );
}
