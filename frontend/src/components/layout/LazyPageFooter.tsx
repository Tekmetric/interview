import { lazy, Suspense } from 'react';

const PageFooter = lazy(() =>
  import('./PageFooter').then((module) => ({ default: module.PageFooter }))
);

export function LazyPageFooter() {
  return (
    <Suspense
      fallback={
        <footer className="min-h-[57px] shrink-0" aria-hidden="true" />
      }
    >
      <PageFooter />
    </Suspense>
  );
}
