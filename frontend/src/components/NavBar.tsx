import type { StationSelectOption } from '@customTypes/components/StationSelect';
import { StationSelect } from '@components/StationSelect';
import { METRO_COLORS, STATIONS_BY_LINE } from '@constants/wmata';
import { Subway } from '@phosphor-icons/react';
import { Link } from '@tanstack/react-router';
import { useLocalStorage } from '@uidotdev/usehooks';

export function NavBar() {
  const [history] = useLocalStorage('MetroBuddy.history', '[]');

  const historyOptions: StationSelectOption[] = JSON.parse(history!);

  return (
    <div className="p-2 flex gap-2 w-full items-center">
      <Link
        to="/"
        title="Home"
        className="font-bold"
        activeProps={ { className: 'font-bold' } }
      >
        { ({ isActive }) => {
          return (
            <Subway
              size={ 24 }
              weight={ isActive ? 'bold' : 'regular' }
              className="hover:bg-gray-200 rounded-md"
            />
          );
        } }
      </Link>
      <Link
        title="All Trains"
        to="/metro/$stationCodes"
        params={ {
          stationCodes: 'All'
        } }
        activeProps={ { className: 'font-bold' } }
        preload="intent"
      >
        All Trains
      </Link>
      {Object.entries(STATIONS_BY_LINE).map(([line, stations]) => (
        <Link
          className={ `${ METRO_COLORS[line as keyof typeof METRO_COLORS] } rounded p-1` }
          key={ line }
          title={ line }
          to="/metro/$stationCodes"
          params={ {
            stationCodes: stations.map(station => station.Code).join(',')
          } }
          search={ { line } }
          activeProps={ { className: 'font-bold' } }
          preload="intent"
        >
          {line}
        </Link>
      ))}
      <div className="ml-auto">
        <StationSelect
          autoFocus={ false }
          className="w-80"
          isClearable={ false }
          isDisabled={ historyOptions.length === 0 }
          options={ historyOptions }
          placeholder="History"
        />
      </div>
    </div>
  );
}
