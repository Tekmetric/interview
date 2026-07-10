import { SORT_OPTIONS } from '../../hooks/sortOptions';

interface SortDropdownProps {
  value: string;
  onChange: (optionId: string) => void;
  className?: string;
}

export function SortDropdown({ value, onChange, className }: SortDropdownProps) {
  return (
    <select
      aria-label="Sort products"
      value={value}
      onChange={(event) => onChange(event.target.value)}
      className={`cursor-pointer rounded border border-border-input bg-elevated px-3 py-2 text-sm text-text-secondary focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-cta-focus ${className ?? ''}`.trim()}
    >
      {SORT_OPTIONS.map((option) => (
        <option key={option.id} value={option.id}>
          Sort: {option.label}
        </option>
      ))}
    </select>
  );
}
