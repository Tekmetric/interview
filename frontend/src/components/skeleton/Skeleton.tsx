import { usePrefersReducedMotion } from '../../hooks/usePrefersReducedMotion';

interface SkeletonProps {
  className?: string;
}

export function Skeleton({ className }: SkeletonProps) {
  const prefersReducedMotion = usePrefersReducedMotion();

  return (
    <div
      className={[
        'rounded bg-neutral-200',
        prefersReducedMotion ? '' : 'animate-pulse',
        className,
      ]
        .filter(Boolean)
        .join(' ')}
      aria-hidden="true"
    />
  );
}
