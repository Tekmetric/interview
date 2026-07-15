// Wraps a native <select> (accessible by default, no custom listbox needed).
// `label` is required and visually hidden unless `showLabel` is set.
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
      <select
        id={id}
        value={value}
        onChange={(e) => onChange(e.target.value)}
        className={`rounded-md border border-line bg-surface ${sizeClass} text-ink transition-colors hover:bg-surface-2 ${fluid ? 'w-full sm:w-auto' : ''}`}
      >
        {options.map((opt) => (
          <option key={opt.value} value={opt.value}>
            {opt.label}
          </option>
        ))}
      </select>
    </div>
  );
}
