import type { EntityLiveData } from '@/types/parks-api';
import { Card, CardContent, CardFooter, CardTitle } from './ui/card';
import { Link } from '@tanstack/react-router';
import { Badge } from './ui/badge';

export default function ParkPreview({ park }: { park: EntityLiveData }) {
  const statusText = park.status === 'OPERATING' ? 'Open' : 'Closed';
  const badgeVariant = statusText.toLowerCase() as 'open' | 'closed';

  return (
    <Link to="/parks/$parkId" params={{ parkId: park.id }} className="rounded-xl">
      <Card className="h-full justify-between transition-colors hover:bg-slate-50 in-focus-visible:bg-slate-50">
        <CardContent className="flex flex-col justify-between gap-2">
          <CardTitle className="mb-2 text-lg">{park.name} &reg;</CardTitle>
        </CardContent>
        <CardFooter>
          <Badge variant={badgeVariant}>{statusText}</Badge>
        </CardFooter>
      </Card>
    </Link>
  );
}
