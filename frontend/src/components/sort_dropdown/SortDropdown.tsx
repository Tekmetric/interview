import { SORT_OPTIONS } from '../../hooks/sortOptions';
import { formControlClassName } from '../../styles/formControl';

const selectClassName = `${formControlClassName} cursor-pointer px-3 py-2 text-text-secondary`;

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
      className={[selectClassName, className].filter(Boolean).join(' ')}
    >
      {SORT_OPTIONS.map((option) => (
        <option key={option.id} value={option.id}>
          Sort: {option.label}
        </option>
      ))}
    </select>
  );
}
