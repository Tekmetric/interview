import { StationSelect } from '@components/StationSelect';
import { Subway } from '@phosphor-icons/react';
import { Link } from '@tanstack/react-router';

export function NavBar() {
  return (
    <div className="py-2 px-4 flex w-full items-center justify-between">
      <Link
        to="/"
        title="Home"
        className="font-bold"
        activeProps={ { className: 'font-bold' } }
      >
        { ({ isActive }) => {
          return (
            <div className="bg-white hover:bg-gray-200 rounded px-2 py-1 shadow">
              <Subway
                size={ 32 }
                weight={ isActive ? 'bold' : 'regular' }
                className="p-1 inline-block mr-1"
              />
              Home
            </div>
          );
        } }
      </Link>
      <StationSelect className="w-80" placeholder="Find your station | ⌘/ctrl + k" />
    </div>
  );
}
