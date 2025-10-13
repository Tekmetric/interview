import RidePreview from './RidePreview';
import type { EntityLiveData } from '@/types/parks-api';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from './ui/select';
import { useMemo, useState } from 'react';
import { ArrowDownIcon, ArrowUpIcon } from 'lucide-react';
import { Button } from './ui/button';
import spacetime from 'spacetime';
import { Input } from '@/components/ui/input';

type SortCategory = 'waitTime' | 'name' | 'lastUpdatedMinutes';

export default function RideWaitTimeList({
  liveData,
  timezone,
}: {
  liveData: Array<EntityLiveData>;
  timezone: string;
}) {
  const [searchTerm, setSearchTerm] = useState('');
  const [sort, setSort] = useState<{ direction: 'asc' | 'desc'; category: SortCategory }>({
    direction: 'asc',
    category: 'lastUpdatedMinutes',
  });

  function calcLastUpdated(ride: EntityLiveData): number {
    const lastUpdated = spacetime(ride.lastUpdated);
    const now = spacetime.now(timezone);
    const diff = lastUpdated.diff(now);
    if (diff.hours > 0) {
      return diff.hours * 60 + diff.minutes;
    } else {
      return diff.minutes;
    }
  }

  const mappedData = useMemo(() => {
    return liveData.map((ride) => {
      const waitTime = ride.queue?.STANDBY?.waitTime || null;
      return { ...ride, waitTime, lastUpdatedMinutes: calcLastUpdated(ride) };
    });
  }, [liveData, timezone]);

  const rideData = mappedData.filter((ride) => {
    return (
      ride.status === 'OPERATING' && ride.waitTime && ride.name.toLowerCase().includes(searchTerm.toLowerCase().trim())
    );
  });

  const sortedRides = [...rideData].sort((a, b) => {
    let aValue: number | string = 0;
    let bValue: number | string = 0;
    switch (sort.category) {
      case 'waitTime':
        aValue = a.waitTime || 0;
        bValue = b.waitTime || 0;
        break;
      case 'name':
        aValue = a.name.trim().toLocaleLowerCase().replace(`"`, '');
        bValue = b.name.trim().toLocaleLowerCase().replace(`"`, '');
        break;
      case 'lastUpdatedMinutes':
        aValue = a.lastUpdatedMinutes;
        bValue = b.lastUpdatedMinutes;
        break;
      default:
        aValue = a.lastUpdatedMinutes;
        bValue = b.lastUpdatedMinutes;
        break;
    }
    if (aValue < bValue) return sort.direction === 'asc' ? -1 : 1;
    if (aValue > bValue) return sort.direction === 'asc' ? 1 : -1;
    return 0;
  });

  function onSortCategoryChange(value: SortCategory) {
    setSort({ ...sort, category: value });
  }

  return (
    <>
      <div className="flex gap-1.5 justify-between items-center mb-3">
        <Input
          type="text"
          placeholder="Search for a ride"
          aria-label="Search for a ride"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="max-w-1/2"
        />
        <div className="flex gap-1.5 justify-between items-center">
          <Select value={sort.category} onValueChange={onSortCategoryChange} aria-label="Sort by">
            <SelectTrigger className="min-w-[140px]">
              <SelectValue placeholder="Sort by..." />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="waitTime">Wait time</SelectItem>
              <SelectItem value="name">Name</SelectItem>
              <SelectItem value="lastUpdatedMinutes">Last Updated</SelectItem>
            </SelectContent>
          </Select>
          <Button
            variant="outline"
            size="icon"
            aria-label={sort.direction === 'asc' ? 'Sort descending' : 'Sort ascending'}
            onClick={() => setSort({ ...sort, direction: sort.direction === 'asc' ? 'desc' : 'asc' })}
          >
            {sort.direction === 'asc' && <ArrowUpIcon />}
            {sort.direction === 'desc' && <ArrowDownIcon />}
          </Button>
        </div>
      </div>

      <div className="flex flex-col gap-4">
        {sortedRides.length > 0 && sortedRides.map((item) => <RidePreview key={item.id} ride={item} />)}
        {sortedRides.length === 0 && <p className="text-center p-10 text-lg">No ride wait times available.</p>}
      </div>
    </>
  );
}
