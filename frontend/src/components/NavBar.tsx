import { StationSelect } from '@components/StationSelect';
import { Button } from '@components/ui/button';
import { ModeToggle } from '@components/ui/mode-toggle';
import { Subway } from '@phosphor-icons/react';
import { Link } from '@tanstack/react-router';

export function NavBar() {
  return (
    <div className="py-2 px-4 flex w-full items-center justify-between shadow-md rounded  dark:shadow-gray-800">
      <Link
        to="/"
        title="Home"
        className="font-bold"
        activeProps={ { className: 'font-bold' } }
      >
        { ({ isActive }) => {
          return (
            <Button variant="outline" size="lg">
              <Subway
                size={ 32 }
                weight={ isActive ? 'bold' : 'regular' }
                className="inline-block mr-1"
              />
              Home
            </Button>
          );
        } }
      </Link>
      <div className="flex gap-2">
        <StationSelect className="w-80" placeholder="Find your station | ⌘/ctrl + k" />
        <ModeToggle />
      </div>
    </div>
  );
}
