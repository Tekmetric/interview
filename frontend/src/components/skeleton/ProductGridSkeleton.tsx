import { Skeleton } from './Skeleton';

export function ProductGridSkeleton() {
  return (
    <div role="status" aria-live="polite" aria-busy="true">
      <span className="sr-only">Loading products</span>
      <Skeleton className="min-h-[60vh] w-full border border-neutral-200" />
    </div>
  );
}
