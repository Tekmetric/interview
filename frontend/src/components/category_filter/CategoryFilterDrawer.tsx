import { Button } from '../button/Button';
import { Drawer } from '../drawer/Drawer';
import type { ProductCategory } from '../../hooks/types';
import { CategoryRadioList } from './CategoryRadioList';
import {
  CATEGORY_DRAWER_DISABLED_HINT_ID,
  CATEGORY_FILTER_DRAWER_PANEL_ID,
  DISABLED_HINT_TEXT,
} from './categoryFilterConstants';

interface CategoryFilterDrawerProps {
  isOpen: boolean;
  categories: ProductCategory[];
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
  error,
  isSearchActive,
  draftSlug,
  onDraftChange,
  onApply,
  onCancel,
}: CategoryFilterDrawerProps) {
  const canApply = !error;

  return (
    <Drawer
      isOpen={isOpen}
      onClose={onCancel}
      title="Categories"
      titleId="category-drawer-title"
      panelId={CATEGORY_FILTER_DRAWER_PANEL_ID}
      closeAriaLabel="Close category filter"
      panelClassName="drawer-panel--full-height"
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
        aria-describedby={isSearchActive ? CATEGORY_DRAWER_DISABLED_HINT_ID : undefined}
        className="m-0 min-w-0 border-0 p-0"
      >
        {isSearchActive && (
          <p
            id={CATEGORY_DRAWER_DISABLED_HINT_ID}
            className="mb-2 text-sm text-text-muted"
            title={DISABLED_HINT_TEXT}
          >
            {DISABLED_HINT_TEXT}
          </p>
        )}

        {error && (
          <p role="alert" className="text-sm text-error">
            {error}
          </p>
        )}

        {!error && (
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
