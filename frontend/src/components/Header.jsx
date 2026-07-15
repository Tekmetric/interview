import { NavLink } from 'react-router-dom';
import { useCollection } from '../context/CollectionContext';
import ThemeToggle from './ThemeToggle';

// Active link is underlined + weighted (not signalled by color alone).
function navClass({ isActive }) {
  return [
    'inline-flex items-center gap-1 rounded-md px-2 py-1 text-sm transition-colors hover:text-accent',
    isActive
      ? 'font-medium text-accent underline underline-offset-4'
      : 'text-muted',
  ].join(' ');
}

export default function Header() {
  const { count } = useCollection();

  return (
    <header className="border-b border-line bg-surface/80 backdrop-blur">
      <div className="mx-auto flex max-w-[1600px] flex-wrap items-center justify-between gap-x-4 gap-y-2 px-4 py-3 sm:px-6">
        <NavLink to="/" className="flex items-center gap-2 font-bold text-ink">
          <img
            src={`${import.meta.env.BASE_URL}logo.png`}
            alt=""
            className="size-7"
          />
          <span>Meet the Met</span>
        </NavLink>

        <nav
          aria-label="Primary"
          className="flex flex-wrap items-center gap-x-1 gap-y-2"
        >
          <NavLink to="/" end className={navClass}>
            Search
          </NavLink>
          <NavLink to="/collection" className={navClass}>
            Collection
            {count > 0 && (
              <span className="rounded-full bg-accent px-1.5 py-0.5 text-xs font-medium text-canvas">
                {count}
              </span>
            )}
          </NavLink>
          <span className="mx-2 hidden h-5 w-px bg-line sm:block" aria-hidden="true" />
          <ThemeToggle />
        </nav>
      </div>
    </header>
  );
}
