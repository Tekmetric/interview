import type { AvailabilityStatus } from '../../hooks/types';
import { getAvailabilityBadgeLabel } from '../../utils/availabilityStatus';

interface AvailabilityBadgeProps {
  availabilityStatus: AvailabilityStatus;
}

export function AvailabilityBadge({ availabilityStatus }: AvailabilityBadgeProps) {
  const label = getAvailabilityBadgeLabel(availabilityStatus);

  if (!label) {
    return null;
  }

  return (
    <span
      className="absolute left-2 top-2 z-[2] rounded bg-badge px-2 py-1 text-xs font-medium text-on-badge"
    >
      {label}
    </span>
  );
}
