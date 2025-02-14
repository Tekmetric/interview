import { METRO_COLORS, METRO_LINE_LABELS } from '@constants/wmata';
import { Link } from '@tanstack/react-router';

export function MetroLineSelect({ className, stationCodes, lines }: { lines: string[]; stationCodes: string; className?: string }) {
  return (
    <div className={ `flex gap-2 ${ className }` }>
      <Link
        title="Show all lines"
        to="/metro/$stationCodes"
        params={ {
          stationCodes
        } }
        className="bg-white h-[32px] p-1 align-middle hover:bg-gray-200 rounded shadow"
        activeProps={ { className: 'font-bold' } }
        preload="intent"
      >
        All lines
      </Link>
      {lines.map(line => (
        <Link
          className={ `inline-block w-[32px] h-[32px] text-center ${ METRO_COLORS[line as keyof typeof METRO_COLORS] } rounded p-1 shadow` }
          key={ line }
          title={ `Show only ${ METRO_LINE_LABELS[line as keyof typeof METRO_LINE_LABELS] } Line trains` }
          to="/metro/$stationCodes"
          params={ {
            stationCodes
          } }
          search={ { line } }
          activeProps={ { className: 'font-bold' } }
          preload="intent"
        >
          {line}
        </Link>
      ))}
    </div>
  );
}
