import { Card, CardContent, CardTitle } from '@/components/ui/card';
import { cn } from '@/lib/utils';
import type { EntityLiveData } from '@/types/parks-api';
import { useMemo } from 'react';
import { Badge } from './ui/badge';
import { cva } from 'class-variance-authority';

interface RidePreviewProps {
  ride: EntityLiveData & { waitTime: number | null; lastUpdatedMinutes: number };
}

const waitTimeVariants = cva('rounded-full w-full h-full flex flex-col justify-center items-center', {
  variants: {
    variant: {
      default: 'bg-gray-100 text-gray-800',
      long: 'bg-red-100 text-red-800',
      medium: 'bg-yellow-100 text-yellow-800',
      short: 'bg-green-100 text-green-800',
    },
  },
  defaultVariants: {
    variant: 'default',
  },
});

export default function RidePreview({ ride }: RidePreviewProps) {
  const lastUpdated = useMemo(() => {
    if (ride.lastUpdatedMinutes === 0) {
      return 'Just now';
    }
    if (ride.lastUpdatedMinutes < 60) {
      return `${ride.lastUpdatedMinutes} minute${ride.lastUpdatedMinutes > 1 ? 's' : ''} ago`;
    }

    const hours = Math.floor(ride.lastUpdatedMinutes / 60);
    return `${hours} hour${hours > 1 ? 's' : ''}  ago`;
  }, [ride.lastUpdatedMinutes]);

  const waitTimeVariant = useMemo(() => {
    if (!ride.waitTime) return 'default';
    if (ride.waitTime > 60) return 'long';
    if (ride.waitTime > 30) return 'medium';
    return 'short';
  }, [ride.waitTime]);

  return (
    <Card>
      <CardContent className="flex justify-between gap-3">
        <div className="inline-flex w-20 h-20">
          <span className={cn(waitTimeVariants({ variant: waitTimeVariant }))} data-testid="wait-time">
            <span className="text-3xl font-medium" >{ride.waitTime || '-'}</span>
            <span>min</span>
          </span>
        </div>
        <div className="flex flex-col gap-3 justify-center">
          <CardTitle className="self-center text-right leading-tight text-sm lg:text-lg" data-testid="ride-name">{ride.name}</CardTitle>
          <Badge variant="secondary" className="ml-auto" data-testid="last-updated">
            {lastUpdated}
          </Badge>
        </div>
      </CardContent>
    </Card>
  );
}
