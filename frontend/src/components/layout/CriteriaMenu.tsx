import { useEffect, useRef, useState } from 'react';
import { Button } from '../button/Button';
import { useTheme } from '../../theme/ThemeProvider';

const DEV_OPTIONS = [
  { id: 'infiniteScroll', label: 'Toggle infinite scroll' },
  { id: 'oppositeColorScheme', label: 'Use opposite color scheme' },
  { id: 'spanish', label: 'Translate to Spanish' },
] as const;

type DevOptionId = (typeof DEV_OPTIONS)[number]['id'];

export function CriteriaMenu() {
  const { useOppositeScheme, setUseOppositeScheme } = useTheme();
  const [isOpen, setIsOpen] = useState(false);
  const [checkedOptions, setCheckedOptions] = useState<
    Record<Exclude<DevOptionId, 'oppositeColorScheme'>, boolean>
  >({
    infiniteScroll: false,
    spanish: false,
  });
  const containerRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (!isOpen) {
      return;
    }

    function handleKeyDown(event: KeyboardEvent) {
      if (event.key === 'Escape') {
        setIsOpen(false);
      }
    }

    function handlePointerDown(event: MouseEvent) {
      if (
        containerRef.current &&
        !containerRef.current.contains(event.target as Node)
      ) {
        setIsOpen(false);
      }
    }

    document.addEventListener('keydown', handleKeyDown);
    document.addEventListener('mousedown', handlePointerDown);

    return () => {
      document.removeEventListener('keydown', handleKeyDown);
      document.removeEventListener('mousedown', handlePointerDown);
    };
  }, [isOpen]);

  function handleCheckboxChange(id: DevOptionId, checked: boolean) {
    if (id === 'oppositeColorScheme') {
      setUseOppositeScheme(checked);
      return;
    }

    setCheckedOptions((previous) => ({
      ...previous,
      [id]: checked,
    }));
  }

  function isOptionChecked(id: DevOptionId) {
    if (id === 'oppositeColorScheme') {
      return useOppositeScheme;
    }

    return checkedOptions[id];
  }

  return (
    <nav aria-label="Developer options" ref={containerRef} className="relative">
      <Button
        variant="secondary"
        aria-haspopup="menu"
        aria-expanded={isOpen}
        onClick={() => setIsOpen((open) => !open)}
      >
        Toggle
      </Button>

      {isOpen && (
        <div className="absolute right-0 bottom-full z-10 mb-1 min-w-52 rounded border border-border bg-elevated p-2">
          <ul className="space-y-1">
            {DEV_OPTIONS.map((option) => (
              <li key={option.id}>
                <label className="flex cursor-pointer items-center gap-2 rounded px-2 py-1.5 text-sm text-text-secondary hover:bg-hover">
                  <input
                    type="checkbox"
                    checked={isOptionChecked(option.id)}
                    onChange={(event) =>
                      handleCheckboxChange(option.id, event.target.checked)
                    }
                    className="h-4 w-4 rounded border-border-input"
                  />
                  {option.label}
                </label>
              </li>
            ))}
          </ul>
        </div>
      )}
    </nav>
  );
}
