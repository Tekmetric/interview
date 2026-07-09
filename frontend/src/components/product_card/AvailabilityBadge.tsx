import { getAvailabilityBadgeLabel } from '../../utils/availabilityStatus';

interface AvailabilityBadgeProps {
  availabilityStatus: string;
}

export function AvailabilityBadge({ availabilityStatus }: AvailabilityBadgeProps) {
  const label = getAvailabilityBadgeLabel(availabilityStatus);

  if (!label) {
    return null;
  }

  return (
    <span
      className="absolute left-2 top-2 z-[2] rounded bg-neutral-900/80 px-2 py-1 text-xs font-medium text-white"
    >
      {label}
    </span>
  );
}
