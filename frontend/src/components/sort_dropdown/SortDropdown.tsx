import { SORT_OPTIONS } from '../../hooks/types';

interface SortDropdownProps {
  value: string;
  onChange: (optionId: string) => void;
}

export function SortDropdown({ value, onChange }: SortDropdownProps) {
  return (
    <select
      aria-label="Sort products"
      value={value}
      onChange={(event) => onChange(event.target.value)}
      className="cursor-pointer rounded border border-neutral-300 bg-white px-3 py-2 text-sm text-neutral-700 focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue-600"
    >
      {SORT_OPTIONS.map((option) => (
        <option key={option.id} value={option.id}>
          Sort: {option.label}
        </option>
      ))}
    </select>
  );
}
