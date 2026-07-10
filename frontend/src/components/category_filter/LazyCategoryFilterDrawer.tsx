import { lazy, Suspense } from 'react';
import type { ProductCategory } from '../../types/product';

const CategoryFilterDrawer = lazy(() =>
  import('./CategoryFilterDrawer').then((module) => ({
    default: module.CategoryFilterDrawer,
  }))
);

interface LazyCategoryFilterDrawerProps {
  isOpen: boolean;
  categories: ProductCategory[];
  error: string | null;
  isSearchActive: boolean;
  draftSlug: string | null;
  onDraftChange: (slug: string | null) => void;
  onApply: () => void;
  onCancel: () => void;
}

export function LazyCategoryFilterDrawer(props: LazyCategoryFilterDrawerProps) {
  if (!props.isOpen) {
    return null;
  }

  return (
    <Suspense fallback={null}>
      <CategoryFilterDrawer {...props} />
    </Suspense>
  );
}
