import { usePrefersReducedMotion } from '../../hooks/usePrefersReducedMotion';

interface SkeletonProps {
  className?: string;
}

export function Skeleton({ className }: SkeletonProps) {
  const prefersReducedMotion = usePrefersReducedMotion();

  return (
    <div
      className={[
        'rounded bg-disabled-bg',
        prefersReducedMotion ? '' : 'animate-pulse',
        className,
      ]
        .filter(Boolean)
        .join(' ')}
      aria-hidden="true"
    />
  );
}
