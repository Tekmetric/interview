import type { ProductCategory } from '../../hooks/types';

const DISABLED_HINT_ID = 'category-filter-disabled-hint';
const DISABLED_HINT_TEXT =
  'Category filtering is not supported while search query is active';

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
  return (
    <aside aria-label="Product filters" className="lg:w-56 shrink-0">
      <fieldset
        disabled={isSearchActive}
        aria-describedby={isSearchActive ? DISABLED_HINT_ID : undefined}
        className="m-0 min-w-0 border-0 p-0"
      >
        <legend className="mb-2 text-sm font-semibold text-neutral-900">
          Categories
        </legend>

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
          <ul className="m-0 list-none space-y-1 p-0">
            <li>
              <label className="flex cursor-pointer items-center gap-2 rounded px-2 py-1.5 text-sm text-neutral-700 hover:bg-neutral-50 has-disabled:cursor-not-allowed has-disabled:opacity-60">
                <input
                  type="radio"
                  name="product-category"
                  value=""
                  checked={value === null}
                  onChange={() => onChange(null)}
                  className="h-4 w-4 border-neutral-300 focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue-600"
                />
                All categories
              </label>
            </li>
            {categories.map((category) => (
              <li key={category.slug}>
                <label className="flex cursor-pointer items-center gap-2 rounded px-2 py-1.5 text-sm text-neutral-700 hover:bg-neutral-50 has-disabled:cursor-not-allowed has-disabled:opacity-60">
                  <input
                    type="radio"
                    name="product-category"
                    value={category.slug}
                    checked={value === category.slug}
                    onChange={() => onChange(category.slug)}
                    className="h-4 w-4 border-neutral-300 focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue-600"
                  />
                  {category.name}
                </label>
              </li>
            ))}
          </ul>
        )}
      </fieldset>
    </aside>
  );
}
