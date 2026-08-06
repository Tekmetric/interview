const BASE =
  'inline-flex items-center justify-center gap-1.5 whitespace-nowrap rounded-md font-medium transition disabled:opacity-60 disabled:pointer-events-none';

const VARIANTS = {
  primary: 'bg-accent text-canvas hover:opacity-90',
  secondary: 'border border-line text-ink hover:bg-surface-2',
  danger: 'border border-danger/40 text-danger hover:bg-danger/10',
};

const SIZES = {
  sm: 'px-3 py-1.5 text-sm',
  md: 'px-4 py-2 text-sm',
  lg: 'px-5 py-2.5',
};

export default function Button({
  variant = 'secondary',
  size = 'md',
  type = 'button',
  className = '',
  ...props
}) {
  return (
    <button
      type={type}
      className={`${BASE} ${VARIANTS[variant]} ${SIZES[size]} ${className}`}
      {...props}
    />
  );
}

const ICON_BASE =
  'inline-flex shrink-0 items-center justify-center rounded-full border border-line p-2 transition-colors hover:bg-surface-2 disabled:opacity-60';

const ICON_TONES = {
  muted: 'text-muted hover:text-ink',
  ink: 'text-ink',
  accent: 'text-accent',
};

// Round icon-only control. `as="a"` renders a link (external row/modal actions)
// while keeping the same hit area and styling.
export function IconButton({ as: Tag = 'button', tone = 'muted', className = '', ...props }) {
  const typeProp = Tag === 'button' ? { type: 'button' } : {};
  return (
    <Tag
      {...typeProp}
      className={`${ICON_BASE} ${ICON_TONES[tone]} ${className}`}
      {...props}
    />
  );
}
