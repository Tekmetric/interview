import { useTheme } from '../../theme/ThemeProvider';

export function PageFooter() {
  const currentYear = new Date().getFullYear();
  const { useOppositeScheme, setUseOppositeScheme } = useTheme();

  return (
    <footer className="border-t border-border bg-elevated">
      <div className="max-w-7xl mx-auto px-4 py-4 flex flex-col items-center gap-2">
        <p className="text-sm text-text-muted">
          &copy; {currentYear} Productpalooza. All rights reserved.
        </p>
        <label className="flex cursor-pointer items-center gap-2 text-sm text-text-secondary">
          <input
            type="checkbox"
            checked={useOppositeScheme}
            onChange={(event) => setUseOppositeScheme(event.target.checked)}
            className="h-4 w-4 rounded border-border-input"
          />
          Use opposite color scheme
        </label>
      </div>
    </footer>
  );
}
