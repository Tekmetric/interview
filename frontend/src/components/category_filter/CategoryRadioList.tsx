import type { ProductCategory } from '../../hooks/types';

interface CategoryRadioListProps {
  categories: ProductCategory[];
  value: string | null;
  onChange: (slug: string | null) => void;
  name: string;
  disabled?: boolean;
}

export function CategoryRadioList({
  categories,
  value,
  onChange,
  name,
  disabled = false,
}: CategoryRadioListProps) {
  return (
    <ul className="m-0 list-none space-y-1 p-0">
      <li>
        <label className="flex cursor-pointer items-center gap-2 rounded px-2 py-1.5 text-sm text-neutral-700 hover:bg-neutral-50 has-disabled:cursor-not-allowed has-disabled:opacity-60">
          <input
            type="radio"
            name={name}
            value=""
            checked={value === null}
            disabled={disabled}
            onChange={() => onChange(null)}
            className="h-4 w-4 border-neutral-300 focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue-600"
          />
          All Categories
        </label>
      </li>
      {categories.map((category) => (
        <li key={category.slug}>
          <label className="flex cursor-pointer items-center gap-2 rounded px-2 py-1.5 text-sm text-neutral-700 hover:bg-neutral-50 has-disabled:cursor-not-allowed has-disabled:opacity-60">
            <input
              type="radio"
              name={name}
              value={category.slug}
              checked={value === category.slug}
              disabled={disabled}
              onChange={() => onChange(category.slug)}
              className="h-4 w-4 border-neutral-300 focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue-600"
            />
            {category.name}
          </label>
        </li>
      ))}
    </ul>
  );
}
