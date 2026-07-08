import { Button } from '../button/Button';
import { Drawer } from '../drawer/Drawer';
import type { ProductCategory } from '../../hooks/types';
import { CategoryRadioList } from './CategoryRadioList';

const DISABLED_HINT_ID = 'category-drawer-disabled-hint';
const DISABLED_HINT_TEXT =
  'Category filtering is not supported while search query is active';

interface CategoryFilterDrawerProps {
  isOpen: boolean;
  categories: ProductCategory[];
  isLoading: boolean;
  error: string | null;
  isSearchActive: boolean;
  draftSlug: string | null;
  onDraftChange: (slug: string | null) => void;
  onApply: () => void;
  onCancel: () => void;
}

export function CategoryFilterDrawer({
  isOpen,
  categories,
  isLoading,
  error,
  isSearchActive,
  draftSlug,
  onDraftChange,
  onApply,
  onCancel,
}: CategoryFilterDrawerProps) {
  const canApply = !isLoading && !error;

  return (
    <Drawer
      isOpen={isOpen}
      onClose={onCancel}
      title="Categories"
      titleId="category-drawer-title"
      closeAriaLabel="Close category filter"
      footer={
        <>
          <Button variant="secondary" onClick={onCancel}>
            Cancel
          </Button>
          <Button variant="primary" onClick={onApply} disabled={!canApply}>
            Apply Selection
          </Button>
        </>
      }
    >
      <fieldset
        disabled={isSearchActive}
        aria-describedby={isSearchActive ? DISABLED_HINT_ID : undefined}
        className="m-0 min-w-0 border-0 p-0"
      >
        {isSearchActive && (
          <p
            id={DISABLED_HINT_ID}
            className="mb-2 text-sm text-neutral-500"
            title={DISABLED_HINT_TEXT}
          >
            {DISABLED_HINT_TEXT}
          </p>
        )}

        {isLoading && (
          <p role="status" aria-live="polite" className="text-sm text-neutral-600">
            Loading categories...
          </p>
        )}

        {error && (
          <p role="alert" className="text-sm text-red-600">
            {error}
          </p>
        )}

        {!isLoading && !error && (
          <CategoryRadioList
            categories={categories}
            value={draftSlug}
            onChange={onDraftChange}
            name="product-category-drawer"
            disabled={isSearchActive}
          />
        )}
      </fieldset>
    </Drawer>
  );
}
