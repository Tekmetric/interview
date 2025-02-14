import { metroArrivalTimesOptions } from '@api/wmata';
import { MetroLineSelect } from '@components/MetroLineSelect';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow
} from '@components/ui/table';
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

  let upcomingTrains = metroArrivalTimesQuery.data.Trains
    .filter(train => train.Line && train.Line !== 'No' && train.Min && train.Min !== '---')
    .sort((a, b) => a.Line.localeCompare(b.Line));

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
            {linesServedAtStation.length > 1
            && (
              <MetroLineSelect
                className="justify-center py-2"
                stationCodes={ stationCodes }
                lines={ linesServedAtStation }
              />
            )}
            <Table className="mx-auto w-1/2  shadow rounded p-4 bg-white dark:bg-gray-700">
              <TableHeader>
                <TableRow>
                  <TableHead>Train</TableHead>
                  <TableHead>Arrival</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                { upcomingTrains.length > 0
                  ? upcomingTrains.map(train => (
                      <TableRow key={ `${ train.Line }-${ train.LocationCode }-${ train.Destination }-${ train.Min }-${ train.Group }` }>
                        <TableCell className="flex align-center">
                          <Train size="24" className={ `${ METRO_COLORS[train.Line as keyof typeof METRO_COLORS] } rounded mr-2` } />
                          <div>
                            { METRO_LINE_LABELS[train.Line as keyof typeof METRO_LINE_LABELS] } Line to { train.DestinationName ?? train.Destination }
                          </div>
                        </TableCell>
                        <TableCell>
                          { `${ train.Min } ${ Number.isNaN(Number(train.Min)) ? '' : ` min${ train.Min === '1' ? '' : 's' }` }` }
                        </TableCell>
                      </TableRow>
                    ))
                  : (
                      <TableRow>
                        <TableCell className="text-center" colSpan={ 2 }>No trains currently scheduled.</TableCell>
                      </TableRow>
                    ) }
              </TableBody>
            </Table>
          </div>
        )
  );
}
