import { useEffect, useRef, useState } from 'react';
import { Button } from '../button/Button';

const DEV_OPTIONS = [
  { id: 'infiniteScroll', label: 'Toggle infinite scroll' },
  { id: 'darkMode', label: 'Toggle dark mode' },
  { id: 'spanish', label: 'Translate to Spanish' },
] as const;

type DevOptionId = (typeof DEV_OPTIONS)[number]['id'];

export function CriteriaMenu() {
  const [isOpen, setIsOpen] = useState(false);
  const [checkedOptions, setCheckedOptions] = useState<Record<DevOptionId, boolean>>({
    infiniteScroll: false,
    darkMode: false,
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
    setCheckedOptions((previous) => ({
      ...previous,
      [id]: checked,
    }));
  }

  return (
    <nav aria-label="Developer options" ref={containerRef} className="relative">
      <Button
        variant="secondary"
        aria-haspopup="true"
        aria-expanded={isOpen}
        onClick={() => setIsOpen((open) => !open)}
      >
        Toggle
      </Button>

      {isOpen && (
        <div className="absolute right-0 bottom-full z-10 mb-1 min-w-52 rounded border border-neutral-200 bg-white p-2 shadow-md">
          <ul className="space-y-1">
            {DEV_OPTIONS.map((option) => (
              <li key={option.id}>
                <label className="flex cursor-pointer items-center gap-2 rounded px-2 py-1.5 text-sm text-neutral-700 hover:bg-neutral-50">
                  <input
                    type="checkbox"
                    checked={checkedOptions[option.id]}
                    onChange={(event) =>
                      handleCheckboxChange(option.id, event.target.checked)
                    }
                    className="h-4 w-4 rounded border-neutral-300"
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
