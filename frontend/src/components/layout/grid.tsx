import { cn } from '@/lib/utils.ts'

type GridProps = {
  children?: React.ReactNode;
  /** Optional additional Tailwind classes to apply to the wrapper */
  className?: string;
  /** Tailwind gap class (for example: 'gap-4'). Defaults to 'gap-4' */
  gapClass?: string;
};

/**
 * Grid
 * Arranges children into a responsive grid that shows up to 3 columns.
 * Default classes: grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4
 */
export const Grid: React.FC<GridProps> =
  ({
    children, className = '',
    gapClass = 'gap-4'
  }) => {
  const base = 'grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3'
  return <div className={cn([base, className, gapClass])}>{children}</div>
}
