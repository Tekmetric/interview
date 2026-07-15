import { IconChevronDown } from './icons';

// Wraps a native <select> (accessible by default, no custom listbox needed). The
// native caret is hidden (appearance-none) in favor of our own, so the arrow
// spacing is consistent across browsers. `label` is required and visually hidden
// unless `showLabel` is set.
export default function Select({
  id,
  label,
  value,
  onChange,
  options,
  showLabel = false,
  fluid = false,
  sizeClass = 'px-2 py-1.5 text-sm',
  className = '',
}) {
  return (
    <div
      className={`flex items-center gap-2 ${fluid ? 'min-w-0 flex-1 sm:flex-none' : ''} ${className}`}
    >
      <label
        htmlFor={id}
        className={showLabel ? 'text-sm text-muted' : 'sr-only'}
      >
        {label}
      </label>
      <div className={`relative ${fluid ? 'w-full sm:w-auto' : ''}`}>
        <select
          id={id}
          value={value}
          onChange={(e) => onChange(e.target.value)}
          className={`w-full appearance-none rounded-md border border-line-strong bg-surface ${sizeClass} pr-9 text-ink transition-colors hover:bg-surface-2`}
        >
          {options.map((opt) => (
            <option key={opt.value} value={opt.value}>
              {opt.label}
            </option>
          ))}
        </select>
        <IconChevronDown className="pointer-events-none absolute inset-y-0 right-3 my-auto size-4 text-muted" />
      </div>
    </div>
  );
}
