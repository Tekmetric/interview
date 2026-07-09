import { useState } from 'react';
import type { ProductCategory } from '../../hooks/types';
import { Button } from '../button/Button';
import { CategoryFilterDrawer } from './CategoryFilterDrawer';
import { CategoryRadioList } from './CategoryRadioList';
import {
  CATEGORY_FILTER_DISABLED_HINT_ID,
  DISABLED_HINT_TEXT,
} from './categoryFilterConstants';

interface CategoryFilterProps {
  categories: ProductCategory[];
  isLoading: boolean;
  error: string | null;
  isSearchActive: boolean;
  value: string | null;
  onChange: (slug: string | null) => void;
}

export function CategoryFilter({
  categories,
  isLoading,
  error,
  isSearchActive,
  value,
  onChange,
}: CategoryFilterProps) {
  const [isDrawerOpen, setIsDrawerOpen] = useState(false);
  const [draftSlug, setDraftSlug] = useState<string | null>(null);

  function openDrawer() {
    setDraftSlug(value);
    setIsDrawerOpen(true);
  }

  function closeDrawer() {
    setIsDrawerOpen(false);
  }

  function handleApply() {
    onChange(draftSlug);
    closeDrawer();
  }

  return (
    <>
      <aside
        aria-label="Product filters"
        className="hidden lg:block lg:w-56 shrink-0"
      >
        <fieldset
          disabled={isSearchActive}
          aria-describedby={isSearchActive ? CATEGORY_FILTER_DISABLED_HINT_ID : undefined}
          className="m-0 min-w-0 border-0 p-0"
        >
          <legend className="mb-2 text-sm font-semibold text-neutral-900">
            Categories
          </legend>

          {isSearchActive && (
            <p
              id={CATEGORY_FILTER_DISABLED_HINT_ID}
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
              value={value}
              onChange={onChange}
              name="product-category"
              disabled={isSearchActive}
            />
          )}
        </fieldset>
      </aside>

      <div className="lg:hidden">
        <Button
          variant="secondary"
          aria-haspopup="dialog"
          aria-expanded={isDrawerOpen}
          disabled={isSearchActive}
          onClick={openDrawer}
        >
          Filter by Category
        </Button>

        <CategoryFilterDrawer
          isOpen={isDrawerOpen}
          categories={categories}
          isLoading={isLoading}
          error={error}
          isSearchActive={isSearchActive}
          draftSlug={draftSlug}
          onDraftChange={setDraftSlug}
          onApply={handleApply}
          onCancel={closeDrawer}
        />
      </div>
    </>
  );
}
