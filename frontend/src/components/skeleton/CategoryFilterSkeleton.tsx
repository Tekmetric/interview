import { Skeleton } from './Skeleton';

const SKELETON_ROW_COUNT = 10;

interface CategoryFilterSkeletonProps {
  variant?: 'sidebar' | 'list';
}

function CategoryRadioRowsSkeleton() {
  const rowWidths = ['w-24', 'w-20', 'w-28', 'w-[5.5rem]', 'w-[6.5rem]', 'w-[4.5rem]', 'w-[7.5rem]', 'w-20', 'w-24', 'w-[5.25rem]'];

  return (
    <div role="status" aria-live="polite" aria-busy="true">
      <span className="sr-only">Loading categories</span>
      {Array.from({ length: SKELETON_ROW_COUNT }, (_, index) => (
        <div
          key={index}
          className="flex items-center gap-2 rounded px-2 py-1.5"
          aria-hidden="true"
        >
          <Skeleton className="h-4 w-4 shrink-0 rounded-full" />
          <Skeleton className={`h-3.5 ${rowWidths[index] ?? 'w-24'}`} />
        </div>
      ))}
    </div>
  );
}

export function CategoryFilterSkeleton({
  variant = 'sidebar',
}: CategoryFilterSkeletonProps) {
  if (variant === 'list') {
    return <CategoryRadioRowsSkeleton />;
  }

  return (
    <>
      <aside
        aria-label="Product filters"
        className="hidden shrink-0 lg:block lg:w-56"
      >
        <p className="mb-2 text-sm font-semibold text-text">Categories</p>
        <CategoryRadioRowsSkeleton />
      </aside>

      <div className="lg:hidden" aria-hidden="true">
        <Skeleton className="h-[2.375rem] w-[9.5rem]" />
      </div>
    </>
  );
}
