import { Skeleton } from './Skeleton';

export function PaginationSkeleton() {
  return (
    <nav
      className="mt-6 flex flex-wrap items-center justify-center gap-2"
      aria-hidden="true"
    >
      <Skeleton className="h-[2.375rem] w-[5.5rem]" />
      <Skeleton className="h-[2.375rem] w-9" />
      <Skeleton className="h-[2.375rem] w-9" />
      <Skeleton className="h-[2.375rem] w-9" />
      <Skeleton className="h-[2.375rem] w-16" />
    </nav>
  );
}
